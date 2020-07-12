package me.ag.clans.types;

import org.bukkit.OfflinePlayer;

public class ClanInvite {
    private Clan clan;
    private OfflinePlayer invited;

    public ClanInvite(Clan from, OfflinePlayer to) {
        this.clan = from;
        this.invited = to;
    }

    public Clan getClan() {
        return this.clan;
    }

    public OfflinePlayer getInvited() {
        return this.invited;
    }

}
