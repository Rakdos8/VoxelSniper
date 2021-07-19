package com.thevoxelbox.voxelsniper;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;
import com.thevoxelbox.voxelsniper.event.SniperMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperReplaceMaterialChangedEvent;

/**
 *
 */
public class Sniper
{
    private final VoxelSniper plugin;
    private final UUID player;
    private boolean enabled = true;
    private final LinkedList<Undo> undoList = new LinkedList<>();
    private final Map<String, SniperTool> tools = Maps.newHashMap();

    public Sniper(final VoxelSniper plugin, final Player player)
    {
        this.plugin = plugin;
        this.player = player.getUniqueId();
        final SniperTool sniperTool = new SniperTool(this);
        sniperTool.assignAction(SnipeAction.ARROW, Material.ARROW);
        sniperTool.assignAction(SnipeAction.GUNPOWDER, Material.GUNPOWDER);
        tools.put(null, sniperTool);
    }

    public String getCurrentToolId()
    {
        return getToolId((getPlayer().getItemInHand() != null) ? getPlayer().getItemInHand().getType() : null);
    }

    public String getToolId(final Material itemInHand)
    {
        if (itemInHand == null)
        {
            return null;
        }

        for (final Map.Entry<String, SniperTool> entry : tools.entrySet())
        {
            if (entry.getValue().hasToolAssigned(itemInHand))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(player);
    }

    /**
     * Sniper execution call.
     *
     * @param action       Action player performed
     * @param itemInHand   Item in hand of player
     * @param clickedBlock Block that the player targeted/interacted with
     * @param clickedFace  Face of that targeted Block
     * @return true if command visibly processed, false otherwise.
     */
    public boolean snipe(final Action action, final Material itemInHand, final Block clickedBlock, final BlockFace clickedFace)
    {
        final String toolId = getToolId(itemInHand);
        final SniperTool sniperTool = tools.get(toolId);

        switch (action)
        {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return false;
        }

        if (sniperTool.hasToolAssigned(itemInHand))
        {
            if (sniperTool.getCurrentBrush() == null)
            {
                getPlayer().sendMessage("No Brush selected.");
                return true;
            }

            if (!getPlayer().hasPermission(sniperTool.getCurrentBrush().getPermissionNode()))
            {
                getPlayer().sendMessage("You are not allowed to use this brush. You're missing the permission node '" + sniperTool.getCurrentBrush().getPermissionNode() + "'");
                return true;
            }

            final SnipeData snipeData = sniperTool.getSnipeData();
            if (getPlayer().isSneaking())
            {
                final Block targetBlock;
                final SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);

                switch (action)
                {
                    case LEFT_CLICK_BLOCK:
                    case LEFT_CLICK_AIR:
                        if (clickedBlock != null)
                        {
                            targetBlock = clickedBlock;
                        }
                        else
                        {
                            final RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
                            targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                        }

                        switch (snipeAction)
                        {
                            case ARROW:
                                if (targetBlock != null)
                                {
                                    final BlockData originalVoxel = snipeData.getVoxelData();
                                    final BlockData blockData = targetBlock.getBlockData();
                                    snipeData.setVoxelData(blockData);
                                    final SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, originalVoxel, blockData);
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().voxel();
                                    return true;
                                }
                                else
                                {
                                    final int originalVoxel = snipeData.getVoxelId();
                                    snipeData.setVoxelId(0);
                                    final SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(originalVoxel, snipeData.getData()), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().voxel();
                                    return true;
                                }
                            case GUNPOWDER:
                                if (targetBlock != null)
                                {
                                    final byte originalData = snipeData.getData();
                                    snipeData.setData(targetBlock.getData());
                                    final SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getVoxelId(), originalData), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().data();
                                    return true;
                                }
                                else
                                {
                                    final byte originalData = snipeData.getData();
                                    snipeData.setData((byte) 0);
                                    final SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getVoxelId(), originalData), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().data();
                                    return true;
                                }
                            default:
                                break;
                        }
                        break;
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        if (clickedBlock != null)
                        {
                            targetBlock = clickedBlock;
                        }
                        else
                        {
                            final RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
                            targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                        }

                        switch (snipeAction)
                        {
                            case ARROW:
                                if (targetBlock != null)
                                {
                                    final int originalId = snipeData.getReplaceId();
                                    snipeData.setReplaceId(targetBlock.getTypeId());
                                    final SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(originalId, snipeData.getReplaceData()), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().replace();
                                    return true;
                                }
                                else
                                {
                                    final int originalId = snipeData.getReplaceId();
                                    snipeData.setReplaceId(0);
                                    final SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(originalId, snipeData.getReplaceData()), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().replace();
                                    return true;
                                }
                            case GUNPOWDER:
                                if (targetBlock != null)
                                {
                                    final byte originalData = snipeData.getReplaceData();
                                    snipeData.setReplaceData(targetBlock.getData());
                                    final SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getReplaceId(), originalData), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().replaceData();
                                    return true;
                                }
                                else
                                {
                                    final byte originalData = snipeData.getReplaceData();
                                    snipeData.setReplaceData((byte) 0);
                                    final SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getReplaceId(), originalData), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
                                    Bukkit.getPluginManager().callEvent(event);
                                    snipeData.getVoxelMessage().replaceData();
                                    return true;
                                }
                            default:
                                break;
                        }
                        break;
                    default:
                        return false;
                }
            }
            else
            {
                final Block targetBlock;
                final Block lastBlock;
                final SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);

                switch (action)
                {
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        break;
                    default:
                        return false;
                }

                if (clickedBlock != null)
                {
                    targetBlock = clickedBlock;
                    lastBlock = clickedBlock.getRelative(clickedFace);
                    if (lastBlock == null)
                    {
                        getPlayer().sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                        return true;
                    }
                }
                else
                {
                    final RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
                    targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
                    lastBlock = rangeBlockHelper.getLastBlock();

                    if (targetBlock == null || lastBlock == null)
                    {
                        getPlayer().sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                        return true;
                    }
                }

                if (sniperTool.getCurrentBrush() instanceof PerformBrush)
                {
                    final PerformBrush performerBrush = (PerformBrush) sniperTool.getCurrentBrush();
                    performerBrush.initP(snipeData);
                }

                final boolean result = sniperTool.getCurrentBrush().perform(snipeAction, snipeData, targetBlock, lastBlock);
                return result;
            }
        }
        return false;
    }

    public IBrush setBrush(final String toolId, final Class<? extends IBrush> brush)
    {
        if (!tools.containsKey(toolId))
        {
            return null;
        }

        return tools.get(toolId).setCurrentBrush(brush);
    }

    public IBrush getBrush(final String toolId)
    {
        if (!tools.containsKey(toolId))
        {
            return null;
        }

        return tools.get(toolId).getCurrentBrush();
    }

    public IBrush previousBrush(final String toolId)
    {
        if (!tools.containsKey(toolId))
        {
            return null;
        }

        return tools.get(toolId).previousBrush();
    }

    public boolean setTool(final String toolId, final SnipeAction action, final Material itemInHand)
    {
        for (final Map.Entry<String, SniperTool> entry : tools.entrySet())
        {
            if (entry.getKey() != toolId && entry.getValue().hasToolAssigned(itemInHand))
            {
                return false;
            }
        }

        if (!tools.containsKey(toolId))
        {
            final SniperTool tool = new SniperTool(this);
            tools.put(toolId, tool);
        }
        tools.get(toolId).assignAction(action, itemInHand);
        return true;
    }

    public void removeTool(final String toolId, final Material itemInHand)
    {
        if (!tools.containsKey(toolId))
        {
            final SniperTool tool = new SniperTool(this);
            tools.put(toolId, tool);
        }
        tools.get(toolId).unassignAction(itemInHand);
    }

    public void removeTool(final String toolId)
    {
        if (toolId == null)
        {
            return;
        }
        tools.remove(toolId);
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    public void storeUndo(final Undo undo)
    {
        if (VoxelSniper.getInstance().getVoxelSniperConfiguration().getUndoCacheSize() <= 0)
        {
            return;
        }
        if (undo != null && undo.getSize() > 0)
        {
            while (undoList.size() >= plugin.getVoxelSniperConfiguration().getUndoCacheSize())
            {
                this.undoList.pollLast();
            }
            undoList.push(undo);
        }
    }

    public void undo()
    {
        undo(1);
    }

    public void undo(final int amount)
    {
        int sum = 0;
        if (this.undoList.isEmpty())
        {
            getPlayer().sendMessage(ChatColor.GREEN + "There's nothing to undo.");
        }
        else
        {
            for (int x = 0; x < amount && !undoList.isEmpty(); x++)
            {
                final Undo undo = this.undoList.pop();
                if (undo != null)
                {
                    undo.undo();
                    sum += undo.getSize();
                }
                else
                {
                    break;
                }
            }
            getPlayer().sendMessage(ChatColor.GREEN + "Undo successful:  " + ChatColor.RED + sum + ChatColor.GREEN + " blocks have been replaced.");
        }
    }

    public void reset(final String toolId)
    {
        final SniperTool backup = tools.remove(toolId);
        final SniperTool newTool = new SniperTool(this);

        for (final Map.Entry<SnipeAction, Material> entry : backup.getActionTools().entrySet())
        {
            newTool.assignAction(entry.getKey(), entry.getValue());
        }
        tools.put(toolId, newTool);
    }

    public SnipeData getSnipeData(final String toolId)
    {
        return tools.containsKey(toolId) ? tools.get(toolId).getSnipeData() : null;
    }

    public void displayInfo()
    {
        final String currentToolId = getCurrentToolId();
        final SniperTool sniperTool = tools.get(currentToolId);
        final IBrush brush = sniperTool.getCurrentBrush();
        getPlayer().sendMessage("Current Tool: " + ((currentToolId != null) ? currentToolId : "Default Tool"));
        if (brush == null)
        {
            getPlayer().sendMessage("No brush selected.");
            return;
        }
        brush.info(sniperTool.getMessageHelper());
        if (brush instanceof Performer)
        {
            ((Performer) brush).showInfo(sniperTool.getMessageHelper());
        }
    }

    public SniperTool getSniperTool(final String toolId)
    {
        return tools.get(toolId);
    }

    public class SniperTool
    {
        private final BiMap<SnipeAction, Material> actionTools = HashBiMap.create();
        private final ClassToInstanceMap<IBrush> brushes = MutableClassToInstanceMap.create();
        private Class<? extends IBrush> currentBrush;
        private Class<? extends IBrush> previousBrush;
        private final SnipeData snipeData;
        private final Message messageHelper;

        private SniperTool(final Sniper owner)
        {
            this(SnipeBrush.class, new SnipeData(owner));
        }

        private SniperTool(final Class<? extends IBrush> currentBrush, final SnipeData snipeData)
        {
            this.snipeData = snipeData;
            messageHelper = new Message(snipeData);
            snipeData.setVoxelMessage(messageHelper);

            final IBrush newBrushInstance = instanciateBrush(currentBrush);
            if (snipeData.owner().getPlayer().hasPermission(newBrushInstance.getPermissionNode()))
            {
                brushes.put(currentBrush, newBrushInstance);
                this.currentBrush = currentBrush;
            }
        }

        public boolean hasToolAssigned(final Material material)
        {
            return actionTools.containsValue(material);
        }

        public SnipeAction getActionAssigned(final Material itemInHand)
        {
            return actionTools.inverse().get(itemInHand);
        }

        public Material getToolAssigned(final SnipeAction action)
        {
            return actionTools.get(action);
        }

        public void assignAction(final SnipeAction action, final Material itemInHand)
        {
            actionTools.forcePut(action, itemInHand);
        }

        public void unassignAction(final Material itemInHand)
        {
            actionTools.inverse().remove(itemInHand);
        }

        public BiMap<SnipeAction, Material> getActionTools()
        {
            return ImmutableBiMap.copyOf(actionTools);
        }

        public SnipeData getSnipeData()
        {
            return snipeData;
        }

        public Message getMessageHelper()
        {
            return messageHelper;
        }

        public IBrush getCurrentBrush()
        {
            if (currentBrush == null)
            {
                return null;
            }
            return brushes.getInstance(currentBrush);
        }

        public IBrush setCurrentBrush(final Class<? extends IBrush> brush)
        {
            Preconditions.checkNotNull(brush, "Can't set brush to null.");
            IBrush brushInstance = brushes.get(brush);
            if (brushInstance == null)
            {
                brushInstance = instanciateBrush(brush);
                Preconditions.checkNotNull(brushInstance, "Could not instanciate brush class.");
                if (snipeData.owner().getPlayer().hasPermission(brushInstance.getPermissionNode()))
                {
                    brushes.put(brush, brushInstance);
                    previousBrush = currentBrush;
                    currentBrush = brush;
                    return brushInstance;
                }
            }

            if (snipeData.owner().getPlayer().hasPermission(brushInstance.getPermissionNode()))
            {
                previousBrush = currentBrush;
                currentBrush = brush;
                return brushInstance;
            }

            return null;
        }

        public IBrush previousBrush()
        {
            if (previousBrush == null)
            {
                return null;
            }
            return setCurrentBrush(previousBrush);
        }

        private IBrush instanciateBrush(final Class<? extends IBrush> brush)
        {
            try
            {
                return brush.newInstance();
            }
            catch (final InstantiationException e)
            {
                return null;
            }
            catch (final IllegalAccessException e)
            {
                return null;
            }
        }
    }
}
