package me.ag.clans.events;

import me.ag.clans.types.Clan;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClanDeleteEvent extends Event implements Cancellable {
    private Clan clan;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ClanDeleteEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return this.clan;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
