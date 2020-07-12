package me.ag.clans.listener;

import me.ag.clans.types.Clan;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player && victim instanceof Player) {
            Clan clan1 = PlayerUtilities.getPlayerClan((Player) attacker);
            Clan clan2 = PlayerUtilities.getPlayerClan((Player) victim);
            if (clan1 == null) return;
            event.setCancelled(clan1.equals(clan2));
        }
    }
}
