package me.ag.clans.types;

import java.io.IOException;
import java.util.Date;

import me.ag.clans.configuration.ClanMemberConfigurationSection;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClanMember {
    private final Clan clan;
    private final OfflinePlayer player;
    private ClanRole role;
    private int totalKills;
    private final Date joinDate;

    protected ClanMember(@NotNull Clan clan, @NotNull OfflinePlayer player, @NotNull ClanRole role) {
        this.clan = clan;
        this.player = player;
        this.role = role;
        this.joinDate = new Date();
    }

    protected ClanMember(@NotNull Clan clan, @NotNull ClanMemberConfigurationSection configuration) {
        this.clan = clan;
        this.player = configuration.getPlayer();
        this.joinDate = configuration.getJoinDate();
        this.role = configuration.getRole();
        this.setKills(configuration.getKills());
    }

    public Clan getClan() {
        return this.clan;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public ClanRole getRole() {
        return this.role;
    }

    public Date getJoinDate() {
        return this.joinDate;
    }

    public int getKills() {
        return this.totalKills;
    }

    public void setKills(int kills) {
        this.totalKills = kills;
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

    public void promote() {
        ClanRole[] roles = ClanRole.values();
        this.role = roles[Math.min(this.role.priority + 1, roles.length)];
    }

    public void demote() {
        ClanRole[] roles = ClanRole.values();
        this.role = roles[Math.max(this.role.priority - 1, roles.length)];
    }

    public void sendMessage(String... message) {
        Player p = this.player.getPlayer();
        if (p != null) {
            p.sendMessage(message);
        }

    }

    protected void leaveClan(boolean silent) {
        PlayerConfiguration playerConfiguration = PlayerUtilities.getPlayerConfiguration(this.player);
        playerConfiguration.setClan(null);

        try {
            playerConfiguration.save();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        if (!silent) {
            this.sendMessage("You have left " + this.clan.getName() + " clan.");
        }

    }

    public boolean equals(Object object) {
        if (object instanceof ClanMember) {
            return ((ClanMember)object).getPlayer() == this.player;
        } else {
            return false;
        }
    }

    public ClanMemberConfigurationSection toConfiguration() {
        return ClanMemberConfigurationSection.fromClanMember(this);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + "clan='" + this.clan.getName() + "'" + ", player=" + this.player.getUniqueId() + ", role=" + this.role + ']';
    }
}
