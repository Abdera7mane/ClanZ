package me.ag.clans.types;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.events.ClanInviteEvent;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ClanInvitation {
    private final Clan clan;
    private final OfflinePlayer invited;
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public ClanInvitation(@NotNull Clan from, @NotNull OfflinePlayer to) {
        this.clan = from;
        this.invited = to;
    }

    public void send() {
        ClanInviteEvent event = new ClanInviteEvent(this);
        plugin.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            PlayerConfiguration playerConfiguration = PlayerUtilities.getPlayerConfiguration(this.invited);
            if (!playerConfiguration.isInvitedBy(this.clan)) {
                playerConfiguration.addInvitation(this);
            }

        }
    }

    public Clan getClan() {
        return this.clan;
    }

    public OfflinePlayer getInvited() {
        return this.invited;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClanInvitation)) {
            return false;
        } else {
            ClanInvitation obj = (ClanInvitation)object;
            return this.clan.equals(obj.getClan()) && this.invited.equals(obj.getInvited());
        }
    }

}
