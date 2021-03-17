package me.ag.clans.messages.formatter;

import me.ag.clans.ClansPlugin;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

import org.jetbrains.annotations.NotNull;

public class PlayerFormatter implements Formatter {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private final Player player;

    public PlayerFormatter(Player player) {
        this.player = player;
    }

    @NotNull
    @Override
    public String format(@NotNull String message) {
        String formatted = message;

        final String name = this.player.getName();
        final String displayName = this.player.getDisplayName();

        if (plugin.usePAPI()) {
            formatted = PlaceholderAPI.setPlaceholders(this.player, formatted);
        }

        formatted = formatted.replace("{player}", name);
        formatted = formatted.replace("{displayname}", displayName);
        formatted = formatted.replace("{sender}", name);

        return formatted;
    }
}
