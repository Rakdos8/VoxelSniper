package com.thevoxelbox.voxelsniper.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelGoToCommand extends VoxelCommand
{
    public VoxelGoToCommand(final VoxelSniper plugin)
    {
        super("VoxelGoTo", plugin);
        setIdentifier("goto");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        try
        {
            final int x = Integer.parseInt(args[0]);
            final int z = Integer.parseInt(args[1]);
            player.teleport(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z));
            player.sendMessage(ChatColor.GREEN + "Woosh!");
            return true;
        }
        catch (final Exception exception)
        {
            player.sendMessage(ChatColor.RED + "Invalid syntax.");
            return true;
        }
    }
}
