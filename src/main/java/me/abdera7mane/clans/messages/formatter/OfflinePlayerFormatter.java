package me.abdera7mane.clans.messages.formatter;

import me.abdera7mane.clans.ClansPlugin;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class OfflinePlayerFormatter implements Formatter {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private final OfflinePlayer offlinePlayer;

    public OfflinePlayerFormatter(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @NotNull
    @Override
    public String format(@NotNull String message) {
        String formatted = message;

        Player playerInstance = this.offlinePlayer.getPlayer();
        if (playerInstance != null) {
            formatted = new PlayerFormatter(playerInstance).format(formatted);
        } else {
            String name = this.offlinePlayer.getName();
            name = name != null ? name : "unknown";

            if (plugin.usePAPI()) {
                formatted = PlaceholderAPI.setPlaceholders(this.offlinePlayer, formatted);
            }
            formatted = formatted.replace("{player}", name);
            formatted = formatted.replace("{displayname}", name);
        }

        return formatted;
    }
}
