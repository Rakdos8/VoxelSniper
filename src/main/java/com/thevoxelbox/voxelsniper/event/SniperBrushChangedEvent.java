package com.thevoxelbox.voxelsniper.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.IBrush;

/**
 *
 */
public class SniperBrushChangedEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private final Sniper sniper;
    private final IBrush originalBrush;
    private final IBrush newBrush;
    private final String toolId;

    public SniperBrushChangedEvent(final Sniper sniper, final String toolId, final IBrush originalBrush, final IBrush newBrush)
    {
        this.sniper = sniper;
        this.originalBrush = originalBrush;
        this.newBrush = newBrush;
        this.toolId = toolId;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public IBrush getOriginalBrush()
    {
        return originalBrush;
    }

    public IBrush getNewBrush()
    {
        return newBrush;
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
