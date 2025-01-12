package com.thevoxelbox.voxelsniper.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.thevoxelbox.voxelsniper.Sniper;

/**
 *
 */
public class SniperBrushSizeChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final int originalSize;
    private final int newSize;
    private final String toolId;

    public SniperBrushSizeChangedEvent(final Sniper sniper, final String toolId, final int originalSize, final int newSize)
    {
        this.sniper = sniper;
        this.originalSize = originalSize;
        this.newSize = newSize;
        this.toolId = toolId;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public int getOriginalSize()
    {
        return originalSize;
    }

    public int getNewSize()
    {
        return newSize;
    }

    public Sniper getSniper()
    {
        return sniper;
    }

    public String getToolId()
    {
        return toolId;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
