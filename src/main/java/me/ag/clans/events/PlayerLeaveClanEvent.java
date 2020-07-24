package me.ag.clans.events;

import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

public class PlayerLeaveClanEvent extends Event implements Cancellable {
    private final Clan clan;
    private final OfflinePlayer player;
    private final Clan.LeaveReason reason;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public PlayerLeaveClanEvent(OfflinePlayer player, Clan clan, Clan.LeaveReason reason) {
        this.player = player;
        this.clan = clan;
        this.reason = reason;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public Clan getClan() {
        return this.clan;
    }

    public Clan.LeaveReason getReason() {
        return this.reason;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
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
