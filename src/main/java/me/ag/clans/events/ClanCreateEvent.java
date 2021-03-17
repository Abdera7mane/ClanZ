package me.ag.clans.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;


public class ClanCreateEvent extends Event implements Cancellable {
    private final String clanName;
    private final OfflinePlayer leader;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ClanCreateEvent(String name, OfflinePlayer leader) {
        this.clanName = name;
        this.leader = leader;
    }

    @NotNull
    public String getClanName() {
        return this.clanName;
    }

    @NotNull
    public OfflinePlayer getLeader() {
        return this.leader;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
