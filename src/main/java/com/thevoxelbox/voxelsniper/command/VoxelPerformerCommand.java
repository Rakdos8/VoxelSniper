package com.thevoxelbox.voxelsniper.command;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import com.thevoxelbox.voxelsniper.brush.perform.Performer;

public class VoxelPerformerCommand extends VoxelCommand
{
    public VoxelPerformerCommand(final VoxelSniper plugin)
    {
        super("VoxelPerformer", plugin);
        setIdentifier("p");
        setPermission("voxelsniper.sniper");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        final Sniper sniper = plugin.getSniperManager().getSniperForPlayer(player);
        final SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());

        try
        {
            if (args == null || args.length == 0)
            {
                final IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
                if (brush instanceof Performer)
                {
                    ((Performer) brush).parse(new String[]{ "m" }, snipeData);
                }
                else
                {
                    player.sendMessage("This brush is not a performer brush.");
                }
            }
            else
            {
                final IBrush brush = sniper.getBrush(sniper.getCurrentToolId());
                if (brush instanceof Performer)
                {
                    ((Performer) brush).parse(args, snipeData);
                }
                else
                {
                    player.sendMessage("This brush is not a performer brush.");
                }
            }
            return true;
        }
        catch (final Exception exception)
        {
            plugin.getLogger().log(Level.WARNING, "Command error from " + player.getName(), exception);
            return true;
        }
    }
}
