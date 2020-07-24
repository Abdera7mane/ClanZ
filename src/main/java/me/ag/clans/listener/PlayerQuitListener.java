package me.ag.clans.listener;

import java.io.IOException;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;
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

        try {
            configuration.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClansPlugin.cachePlayer(configuration, true);
        Clan clan = configuration.getClan();
        if (clan != null) {
            ClansPlugin.cacheClan(clan, true);

            try {
                clan.save();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

    }
}
