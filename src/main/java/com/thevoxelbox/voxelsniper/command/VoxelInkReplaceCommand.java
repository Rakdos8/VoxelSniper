package com.thevoxelbox.voxelsniper.command;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelInkReplaceCommand extends VoxelCommand
{
    public VoxelInkReplaceCommand(final VoxelSniper plugin)
    {
        super("VoxelInkReplace", plugin);
        setIdentifier("vir");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        final byte dataValue;

        if (args.length == 0)
        {
            final Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
            if (targetBlock != null)
            {
                dataValue = targetBlock.getData();
            }
            else
            {
                return true;
            }
        }
        else
        {
            try
            {
                dataValue = Byte.parseByte(args[0]);
            }
            catch (final NumberFormatException exception)
            {
                player.sendMessage("Couldn't parse input.");
                return true;
            }
        }

        final SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
        snipeData.setReplaceData(dataValue);
        snipeData.getVoxelMessage().replaceData();
        return true;
    }
}
