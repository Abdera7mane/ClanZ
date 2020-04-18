package me.ag.clans.types;

import java.util.Date;

import org.bukkit.OfflinePlayer;


public class ClanMember {
    private OfflinePlayer player;
    public ClanRole role;

    Date joinDate;
    int totalKills;

    public ClanMember(OfflinePlayer player, ClanRole... role) {
        this.player = player;
        this.role = (role.length > 0 ) ? role[0] : ClanRole.MEMBER;
        this.joinDate = new Date();

    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void promote() {
        ClanRole[] roles = ClanRole.values();
        role = roles[Math.min(role.priority + 1, roles.length)];
    }

    public void demote() {
        ClanRole[] roles = ClanRole.values();
        role = roles[Math.max(role.priority - 1, roles.length)];
    }

    public void leaveClan() {

    }


}
