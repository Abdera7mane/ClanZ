package me.ag.clans.util;

import java.io.File;
import java.io.IOException;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUtilities {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    @Nullable
    public static Clan getPlayerClan(OfflinePlayer player) {
        PlayerConfiguration configuration = getPlayerConfiguration(player);
        return configuration.getClan();
    }

    @NotNull
    public static PlayerConfiguration getPlayerConfiguration(@NotNull OfflinePlayer player) {
        PlayerConfiguration configuration = new PlayerConfiguration(player);
        if (plugin.isPlayerCached(player)) {
            configuration = plugin.getPlayerCache(player);
        } else {
            try {
                String uuid = player.getUniqueId().toString();
                String childPath = File.separator + uuid + ".yml";
                File file = new File(PlayerConfiguration.defaultPath, childPath);
                configuration.load(file);
                if (configuration.getKeys(false).size() == 0) {
                    file.delete();
                }
            } catch (InvalidConfigurationException | IOException ignored) {}
        }

        plugin.cachePlayer(configuration, !player.isOnline());
        return configuration;
    }

    public static boolean hasClan(@NotNull OfflinePlayer player) {
        return getPlayerClan(player) != null;
    }
}
