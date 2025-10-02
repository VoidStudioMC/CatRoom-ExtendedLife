package com.destroystokyo.paper.event.server;

import catserver.api.annotation.Backported;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Backported(from = "1.13.2")
public class ServerTickStartEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final int tickNumber;

    public ServerTickStartEvent(final int tickNumber) {
        this.tickNumber = tickNumber;
    }

    /**
     * @return What tick this is going be since start (first tick = 1)
     */
    public int getTickNumber() {
        return this.tickNumber;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
