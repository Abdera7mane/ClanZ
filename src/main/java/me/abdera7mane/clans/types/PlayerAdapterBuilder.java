package me.abdera7mane.clans.types;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerAdapterBuilder {
    private final OfflinePlayer player;
    private Clan clan;
    private Set<ClanInvitation> invitations = new HashSet<>();

    public PlayerAdapterBuilder(@NotNull OfflinePlayer player) {
        this.player = player;
    }

    public PlayerAdapterBuilder setClan(@Nullable Clan clan) {
        this.clan = clan;
        return this;
    }

    public PlayerAdapterBuilder setInvitaions(@NotNull Set<ClanInvitation> invites) {
        this.invitations = invites;
        return this;
    }

    public PlayerAdapterBuilder appendInvitation(@NotNull ClanInvitation invitation) {
        this.invitations.add(invitation);
        return this;
    }

    public OfflinePlayer getSelf() {
        return this.player;
    }

    public Clan getClan() {
        return this.clan;
    }

    public Set<ClanInvitation> getInvitations() {
        return this.invitations;
    }

    public PlayerAdapter build() {
        return new PlayerAdapter(this);
    }

}
