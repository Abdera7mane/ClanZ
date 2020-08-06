package me.ag.clans.events;

import me.ag.clans.types.Clan;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;


public class ClanCreateEvent extends Event implements Cancellable {
    private final Clan clan;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ClanCreateEvent(Clan clan) {
        this.clan = clan;
    }

    @NotNull
    public Clan getClan() {
        return this.clan;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
