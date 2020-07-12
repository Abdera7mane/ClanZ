package me.ag.clans.types;

import me.ag.clans.ClansPlugin;

public enum ClanRole {
    RECRUIT(0),
    MEMBER(1),
    JUNIOR_MEMBER(2),
    SENIOR_MEMBER(3),
    SQUAD_LEADER(4),
    VICE_CAPTAIN(5),
    CAPTAIN(6),
    CO_LEADER(7),
    LEADER(8);

    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public final int priority;

    ClanRole(int priority) {
        this.priority = priority;
    }

    public String getDisplayName() {
        return plugin.getConfig().getString(String.format("clan.roles.%s.name", this.toString().toLowerCase()));
    }

}

