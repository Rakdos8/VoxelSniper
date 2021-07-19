package com.thevoxelbox.voxelsniper.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelHeightCommand extends VoxelCommand
{
    public VoxelHeightCommand(final VoxelSniper plugin)
    {
        super("VoxelHeight", plugin);
        setIdentifier("vh");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        final SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            final int height = Integer.parseInt(args[0]);
            snipeData.setVoxelHeight(height);
            snipeData.getVoxelMessage().height();
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid input.");
            return true;
        }
    }
}
