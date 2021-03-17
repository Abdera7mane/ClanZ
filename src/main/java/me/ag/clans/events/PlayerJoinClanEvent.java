package me.ag.clans.events;

import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

public class PlayerJoinClanEvent extends Event implements Cancellable {
    private final Clan clan;
    private final OfflinePlayer player;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public PlayerJoinClanEvent(OfflinePlayer player, Clan clan) {
        this.player = player;
        this.clan = clan;
    }

    @NotNull
    public OfflinePlayer getPlayer() {
        return this.player;
    }

    @NotNull
    public Clan getClan() {
        return this.clan;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
