package com.thevoxelbox.voxelsniper.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelDefaultCommand extends VoxelCommand
{
    public VoxelDefaultCommand(final VoxelSniper plugin)
    {
        super("VoxelDefault", plugin);
        setIdentifier("d");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        sniper.reset(sniper.getCurrentToolId());
        player.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
        return true;
    }
}
