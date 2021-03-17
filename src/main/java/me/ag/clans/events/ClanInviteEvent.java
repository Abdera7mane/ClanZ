package me.ag.clans.events;

import me.ag.clans.types.ClanInvitation;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

public class ClanInviteEvent extends Event implements Cancellable {
    private final ClanInvitation invitation;
    private boolean isCancelled;
    private static final HandlerList handlers = new HandlerList();

    public ClanInviteEvent(ClanInvitation invitation) {
        this.invitation = invitation;
    }

    @NotNull
    public ClanInvitation getInvitation() {
        return this.invitation;
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
