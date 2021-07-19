package com.thevoxelbox.voxelsniper.command;

import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelUndoCommand extends VoxelCommand
{
    public VoxelUndoCommand(final VoxelSniper plugin)
    {
        super("VoxelUndo", plugin);
        setIdentifier("u");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);

        if (args.length == 1)
        {
            try
            {
                final int amount = Integer.parseInt(args[0]);
                sniper.undo(amount);
            }
            catch (final NumberFormatException exception)
            {
                player.sendMessage("Error while parsing amount of undo. Number format exception.");
            }
        }
        else
        {
            sniper.undo();
        }
        plugin.getLogger().info("Player \"" + player.getName() + "\" used /u");
        return true;
    }
}
