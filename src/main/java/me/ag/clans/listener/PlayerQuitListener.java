package me.ag.clans.listener;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;
import me.ag.clans.util.PlayerUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerConfiguration configuration = PlayerUtilities.getPlayerConfiguration(player);
        ClansPlugin.cachePlayer(configuration);
        Clan clan = configuration.getClan();
        if (clan != null) {
            for (ClanMember member : clan.getMembers()) {
                if (member.getPlayer().isOnline()) return;
            }
            ClansPlugin.log("caching " + clan);
            ClansPlugin.cacheClan(clan);
        }
    }

}
