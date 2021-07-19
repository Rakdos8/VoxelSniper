package com.thevoxelbox.voxelsniper.command;

import org.bukkit.entity.Player;

import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;

public class VoxelChunkCommand extends VoxelCommand
{
    public VoxelChunkCommand(final VoxelSniper plugin)
    {
        super("VoxelChunk", plugin);
        setIdentifier("vchunk");
    }

    @Override
    public boolean onCommand(final Player player, final String[] args)
    {
        player.getWorld().refreshChunk(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        return true;
    }
}
