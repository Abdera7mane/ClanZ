package me.ag.clans.listener;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.MetadataValue;

public class AsyncPlayerChatListener implements Listener {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player eventPlayer = event.getPlayer();
        Clan clan = PlayerUtilities.getPlayerClan(eventPlayer);
        if (clan != null && this.getChatMetaDataValue(eventPlayer)) {
            event.setCancelled(true);

            for(ClanMember member : clan.getMembers()) {
                if (!member.getPlayer().isOnline()) {
                    return;
                }

                Player player = member.getPlayer().getPlayer();
                String format = "[" + clan.getName() + "] " + player.getDisplayName() + ": " + event.getMessage();
                member.sendMessage(format);
            }
        }

    }

    private boolean getChatMetaDataValue(Player player) {
        boolean value = false;
        for (MetadataValue meta : player.getMetadata("chat")) {
            if (meta.getOwningPlugin().getName().equals(plugin.getName())) {
                value = meta.asBoolean();
            }
        }

        return value;
    }
}