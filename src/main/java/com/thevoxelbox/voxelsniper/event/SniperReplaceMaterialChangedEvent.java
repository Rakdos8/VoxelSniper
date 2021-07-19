package com.thevoxelbox.voxelsniper.event;

import org.bukkit.event.HandlerList;
import org.bukkit.material.MaterialData;

import com.thevoxelbox.voxelsniper.Sniper;

/**
 *
 */
public class SniperReplaceMaterialChangedEvent extends SniperMaterialChangedEvent
{
    private static final HandlerList handlers = new HandlerList();

    public SniperReplaceMaterialChangedEvent(final Sniper sniper, final String toolId, final MaterialData originalMaterial, final MaterialData newMaterial)
    {
        super(sniper, toolId, originalMaterial, newMaterial);
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
