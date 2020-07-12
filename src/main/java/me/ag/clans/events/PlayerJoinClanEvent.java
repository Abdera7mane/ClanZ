package me.ag.clans.events;

import me.ag.clans.types.Clan;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinClanEvent extends Event implements Cancellable {
    private Clan clan;
    private OfflinePlayer player;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public PlayerJoinClanEvent(OfflinePlayer player, Clan clan) {
        this.player = player;
        this.clan = clan;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
