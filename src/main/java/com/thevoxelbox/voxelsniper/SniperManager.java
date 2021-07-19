package com.thevoxelbox.voxelsniper;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

/**
 *
 */
public class SniperManager
{
    private final Map<UUID, Sniper> sniperInstances = Maps.newHashMap();
    private final VoxelSniper plugin;

    public SniperManager(final VoxelSniper plugin)
    {
        this.plugin = plugin;
    }

    public Sniper getSniperForPlayer(final Player player)
    {
        if (sniperInstances.get(player.getUniqueId()) == null)
        {
            sniperInstances.put(player.getUniqueId(), new Sniper(plugin, player));
        }
        return sniperInstances.get(player.getUniqueId());
    }
}
