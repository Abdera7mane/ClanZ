package me.ag.clans.util;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class PlayerUtilities {
    private static ClansPlugin plugin = ClansPlugin.getInstance();

    @Nullable
    public static Clan getPlayerClan(OfflinePlayer player) {
        PlayerConfiguration configuration = getPlayerConfiguration(player);
        return configuration.getClan();
    }

    public static PlayerConfiguration getPlayerConfiguration(@NotNull OfflinePlayer player) {
        PlayerConfiguration configuration = new PlayerConfiguration();
        String uuid = player.getUniqueId().toString();
        String childPath = "\\data\\players".replace("\\", File.separator) + uuid + ".yml";
        File file = new File(plugin.getDataFolder(),  childPath);
        if (ClansPlugin.isPlayerCached(player)) {
            configuration = ClansPlugin.getPlayerCache(player);
        } else {
            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (configuration.saveToString().length() == 0) file.delete();
        return configuration;
    }
}
