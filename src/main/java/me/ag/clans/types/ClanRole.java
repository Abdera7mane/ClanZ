package me.ag.clans.types;

import me.ag.clans.Clans;
import org.bukkit.plugin.Plugin;

public enum ClanRole {
    RECRUIT(0),
    MEMBER(1),
    JUNIOR_MEMBER(2),
    SENIOR_MEMBER(3),
    SQUAD_LEADER(4),
    VICE_CAPITAIN(5),
    CAPITAIN(6),
    CO_LEADER(7),
    LEADER(8);

    private Plugin plugin = Clans.getPlugin(Clans.class);

    public final int priority;

    ClanRole(int priority) {
        this.priority = priority;
    }

    public String getDisplayName() {
        return plugin.getConfig().getString(String.format("roles.%s.name", this.toString().toLowerCase()));
    }

}

