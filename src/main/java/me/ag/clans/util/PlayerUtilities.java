package me.ag.clans.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

import org.jetbrains.annotations.NotNull;

public class PlayerUtilities {

    @Nullable
    public static Clan getPlayerClan(OfflinePlayer player) {
        PlayerConfiguration configuration = getPlayerConfiguration(player);
        return configuration.getClan();
    }

    @NotNull
    public static PlayerConfiguration getPlayerConfiguration(@NotNull OfflinePlayer player) {
        PlayerConfiguration configuration = new PlayerConfiguration(player);
        if (ClansPlugin.isPlayerCached(player)) {
            configuration = ClansPlugin.getPlayerCache(player);
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

        ClansPlugin.cachePlayer(configuration, !player.isOnline());
        return configuration;
    }
}
