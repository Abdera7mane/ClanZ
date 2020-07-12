package me.ag.clans.util;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.ClanConfiguration;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.events.ClanCreateEvent;
import me.ag.clans.types.Clan;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class ClanUtilities {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public static boolean createClan(String name, OfflinePlayer leader) {
        Clan clan = new Clan(name, leader);
        ClanCreateEvent event = new ClanCreateEvent(clan);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        PlayerConfiguration playerConfig = ClansPlugin.getPlayerCache(leader);
        playerConfig.set("clan", name);
        clan.save();

        try {
            playerConfig.save();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ClansPlugin.cacheClan(clan);
        return true;
    }
    @Nullable
    public static Clan getClan(String name) {
        Clan clan = null;
        if (ClansPlugin.isClanCached(name)) {
            clan = ClansPlugin.getClanCache(name);
        } else {
            String childPath = "\\data\\clans\\".replace("\\", File.separator) + name + ".yml";
            File file = new File(plugin.getDataFolder(), childPath);
            if (!file.isFile()) {
                return null;
            }
            ClanConfiguration configuration = new ClanConfiguration();
            try {
                configuration.load(file);
                clan = new Clan(name, configuration);
                ClansPlugin.cacheClan(clan);
            } catch (IOException | InvalidConfigurationException ignored) {}
        }

        return clan;
    }
}
