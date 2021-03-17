package me.abdera7mane.clans.types;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClanMemberBuilder {
    private final OfflinePlayer offlinePlayer;
    private ClanRole role;
    private int kills;
    private Date joinDate;

    public ClanMemberBuilder(UUID playerUUID) {
        this(Bukkit.getOfflinePlayer(playerUUID));
    }

    public ClanMemberBuilder(OfflinePlayer player) {
        this.offlinePlayer = player;
        this.setRole(ClanRole.MEMBER)
            .setJoinDate(new Date());
    }

    public ClanMemberBuilder setRole(ClanRole role) {
        this.role = role != null ? role : ClanRole.MEMBER;
        return this;
    }

    public ClanMemberBuilder setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public ClanMemberBuilder setJoinDate(Date joinDate) {
        this.joinDate = joinDate != null ? joinDate : new Date();
        return this;
    }

    public ClanMember build(Clan clan) {
        return new ClanMember(clan, this);
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public int getKills() {
        return this.kills;
    }

    public ClanRole getRole() {
        return this.role;
    }

    public Date getJoinDate() {
        return this.joinDate;
    }
}
