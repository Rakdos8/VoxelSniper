package com.thevoxelbox.voxelsniper.event;

import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.thevoxelbox.voxelsniper.Sniper;

/**
 *
 */
public class SniperMaterialChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final BlockData originalMaterial;
    private final BlockData newMaterial;
    private final String toolId;

    public SniperMaterialChangedEvent(final Sniper sniper, final String toolId, final BlockData originalMaterial, final BlockData newMaterial)
    {
        this.sniper = sniper;
        this.originalMaterial = originalMaterial;
        this.newMaterial = newMaterial;
        this.toolId = toolId;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public BlockData getOriginalMaterial()
    {
        return originalMaterial;
    }

    public BlockData getNewMaterial()
    {
        return newMaterial;
    }

    public Sniper getSniper()
    {
        return sniper;
    }

    public String getToolId()
    {
        return toolId;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
