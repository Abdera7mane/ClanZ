package me.ag.clans.listener;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;
import me.ag.clans.util.PlayerUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerConfiguration configuration = PlayerUtilities.getPlayerConfiguration(player);
        ClansPlugin.cachePlayer(configuration, false);
        Clan clan = configuration.getClan();
        if (clan != null) {
            ClansPlugin.log("loaded clan: " + clan);
            ClansPlugin.cacheClan(clan, false);
        }
    }
}
