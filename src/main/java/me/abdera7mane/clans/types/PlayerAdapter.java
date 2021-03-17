package me.abdera7mane.clans.types;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerAdapter {
    private final OfflinePlayer player;
    private Clan clan;
    private Set<ClanInvitation> invitations = new HashSet<>();

    public PlayerAdapter(OfflinePlayer player) {
        this.player = player;
    }

    public PlayerAdapter(PlayerAdapterBuilder builder) {
        this.player = builder.getSelf();
        this.clan = builder.getClan();
        this.invitations = builder.getInvitations();
    }

    public OfflinePlayer self() {
        return this.player;
    }

    @Nullable
    public Clan getClan() {
        return this.clan;
    }

    public Set<ClanInvitation> getInvitations() {
        return this.invitations;
    }

    public Set<ClanInvitation> getInvitations(@NotNull Clan of) {
        return this.getInvitations().stream()
                .filter(invitation -> invitation.getClan().equals(of))
                .collect(Collectors.toSet());
    }

    public void setInvitations(@NotNull Set<ClanInvitation> invites) {
        this.invitations = invites;
    }

    protected void setClan(@Nullable Clan clan)  {
        this.clan = clan;
    }

    public boolean hasClan() {
        return this.getClan() != null;
    }

    public void addInvitation(@NotNull ClanInvitation invitation) {
        this.getInvitations().add(invitation);
    }

    public void removeInvitation(@NotNull Clan of, @Nullable OfflinePlayer sender) {
        if (clan.hasMember(sender)) {
            this.getInvitations(of).forEach(invitation -> {
                OfflinePlayer invitationSender = invitation.getSender();
                if (Objects.equals(invitationSender, sender)) { this.getInvitations().remove(invitation);
                }
            });
        }
    }

    public void removeAllInvitations(@NotNull Clan of) {
        this.getInvitations(of).forEach(invitation -> this.getInvitations().remove(invitation));
    }

    public boolean isInvitedBy(@NotNull Clan clan) {
        return this.getInvitations(clan).size() > 0;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                  .append("[player=")
                  .append(this.self().getUniqueId())
                  .toString();
    }
}