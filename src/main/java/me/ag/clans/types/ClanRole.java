package me.ag.clans.types;

import me.ag.clans.ClansPlugin;

public enum ClanRole {
    RECRUIT,
    MEMBER,
    JUNIOR_MEMBER,
    SENIOR_MEMBER,
    SQUAD_LEADER,
    VICE_CAPTAIN,
    CAPTAIN,
    CO_LEADER,
    LEADER;

    public String getDisplayName() {
        return ClansPlugin.getInstance().getMessages().getRoleDisplayName(this);
    }

    public ClanRole next() {
        if (this.isHighest()) {
            return null;
        }

        return values()[this.ordinal() + 1];
    }

    public ClanRole previous() {
        if (this.isLowest()) {
            return null;
        }

        return values()[this.ordinal() - 1];
    }

    public boolean isHighest() {
        return this.ordinal() == values().length;
    }

    public boolean isLowest() {
        return this.ordinal() == 0;
    }
}

