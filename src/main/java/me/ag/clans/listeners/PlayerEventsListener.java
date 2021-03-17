package me.ag.clans.listeners;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;
import me.ag.clans.types.PlayerAdapter;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventsListener implements Listener {
    private final ClansPlugin plugin;

    public PlayerEventsListener(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        this.plugin.getStorageService().loadPlayerConfiugration(event.getPlayer());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerAdapter adapter = this.plugin.getPlayerAdapter(player);
        this.plugin.getStorageService().storePlayerAdapter(adapter);

    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player eventPlayer = event.getPlayer();
        Clan clan = this.plugin.getPlayerClan(eventPlayer);
        if (clan != null && PlayerUtilities.getChatMetaDataValue(eventPlayer)) {
            event.setCancelled(true);

            for(ClanMember member : clan.getMembers(false)) {
                if (!member.getOfflinePlayer().isOnline()) {
                    return;
                }

                Player player = member.getOfflinePlayer().getPlayer();
                String format = "[" + clan.getName() + "] " + player.getDisplayName() + ": " + event.getMessage();
                member.sendMessage(format);
            }
        }

    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player && victim instanceof Player) {
            Clan attackerClan = this.plugin.getPlayerClan((Player) attacker);
            Clan victimClan = this.plugin.getPlayerClan((Player) victim);
            if (attackerClan == null) return;
            event.setCancelled(attackerClan.equals(victimClan));
        }
    }

}
