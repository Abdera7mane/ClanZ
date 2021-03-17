package me.abdera7mane.clans.types;

import me.abdera7mane.clans.events.ClanInviteEvent;
import me.abdera7mane.clans.ClansPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanInvitation {
    private final Clan clan;
    private final OfflinePlayer reciever;
    private final OfflinePlayer sender;

    public ClanInvitation(@NotNull Clan from, @NotNull OfflinePlayer to, @Nullable OfflinePlayer sender) {
        this.clan = from;
        this.reciever = to;
        this.sender = sender;
    }

    public void send() {
        ClansPlugin plugin = ClansPlugin.getInstance();
        if (plugin == null)
            throw new RuntimeException("ClanZ plugin isn't enabled");

        ClanInviteEvent event = new ClanInviteEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            PlayerAdapter playerAdapter = plugin.getPlayerAdapter(this.getReciever());
            playerAdapter.addInvitation(this);
        }
    }

    @NotNull
    public Clan getClan() {
        return this.clan;
    }

    @NotNull
    public OfflinePlayer getReciever() {
        return this.reciever;
    }

    @Nullable
    public OfflinePlayer getSender() {
        return this.sender;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClanInvitation)) {
            return false;
        } else {
            ClanInvitation obj = (ClanInvitation)object;
            return this.clan.equals(obj.getClan()) && this.reciever.equals(obj.getReciever());
        }
    }

}
