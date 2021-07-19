package com.thevoxelbox.voxelsniper.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelCenterCommand extends VoxelCommand
{
    public VoxelCenterCommand(final VoxelSniper plugin)
    {
        super("VoxelCenter", plugin);
        setIdentifier("vc");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        final SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            final int center = Integer.parseInt(args[0]);
            snipeData.setcCen(center);
            snipeData.getVoxelMessage().center();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input.");
            return true;
        }
    }
}
