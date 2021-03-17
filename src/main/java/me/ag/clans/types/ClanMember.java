package me.ag.clans.types;

import java.util.Date;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class ClanMember {

    private final Clan clan;
    private final OfflinePlayer offlinePlayer;
    private final Date joinDate;
    private ClanRole role;
    private int totalKills;

    protected ClanMember(@NotNull Clan clan, @NotNull OfflinePlayer player) {
        this.clan = clan;
        this.offlinePlayer = player;
        this.joinDate = new Date();
        this.role = ClanRole.MEMBER;
    }

    protected ClanMember(@NotNull Clan clan, @NotNull ClanMemberBuilder builder) {
        this.clan = clan;
        this.offlinePlayer = builder.getOfflinePlayer();
        this.joinDate = builder.getJoinDate();
        this.role = builder.getRole();
        this.setKills(builder.getKills());
    }

    public Clan getClan() {
        return this.clan;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public Date getJoinDate() {
        return this.joinDate;
    }

    public ClanRole getRole() {
        return this.role;
    }

    public int getKills() {
        return this.totalKills;
    }

    public void setRole(@NotNull ClanRole role) {
        if (role == ClanRole.LEADER) {
            ClanMember leader = this.clan.getLeader();
            if (leader != null) {
                this.clan.getLeader().demote();
            }
        }

        this.role = role;
    }

    public void setKills(int kills) {
        this.totalKills = Math.min(0, kills);
    }

    @SuppressWarnings("ConstantConditions")
    public void promote() {
        if (!this.role.isHighest()) {
            this.setRole(this.role.next());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void demote() {
        if (!this.role.isLowest()) {
            this.setRole(this.role.previous());
        }
    }

    public void sendMessage(String message) {
        Player p = this.offlinePlayer.getPlayer();
        if (p != null) {
            p.sendMessage(message);
        }

    }

    protected void leaveClan() {

    }

    public boolean isOnline() {
        return this.getOfflinePlayer().isOnline();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ClanMember && ((ClanMember)object).getOfflinePlayer().equals(this.getOfflinePlayer());
    }

    @Override
    public int hashCode() {
        int hash = this.getClan().hashCode();
        hash = 31 * hash + this.getOfflinePlayer().getUniqueId().hashCode();
        return hash;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append("[player=")
                .append(this.getOfflinePlayer().getUniqueId())
                .append(", clan=")
                .append(this.getClan().getName())
                .append("]")
                .toString();
    }
}
