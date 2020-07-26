package me.ag.clans.messages.formatter;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerFormatter implements Formatter {
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

        formatted = formatted.replace("{player}", name);
        formatted = formatted.replace("{displayname}", displayName);

        return formatted;
    }
}
