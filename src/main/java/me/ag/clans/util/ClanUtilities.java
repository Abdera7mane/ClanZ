package me.ag.clans.util;

import java.io.File;
import java.io.IOException;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.ClanConfiguration;
import me.ag.clans.configuration.InvalidClanConfigurationException;
import me.ag.clans.events.ClanCreateEvent;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanUtilities {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public static boolean createClan(@NotNull String name, @NotNull OfflinePlayer leader) throws InvalidClanNameException {
        if (!isValidClanName(name)) {
            throw new InvalidClanNameException("");
        }
        Clan clan = new Clan(name, leader);
        ClanCreateEvent event = new ClanCreateEvent(clan);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        try {
            clan.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClansPlugin.cacheClan(clan, !leader.isOnline());
        return true;

    }

    @Nullable
    public static Clan getClan(String name) {
        Clan clan = null;
        if (ClansPlugin.isClanCached(name)) {
            clan = ClansPlugin.getClanCache(name);
        } else {
            String childPath = File.separator + name.toLowerCase() + ".yml";
            File file = new File(ClanConfiguration.defaultPath, childPath);

            try {
                ClanConfiguration configuration = new ClanConfiguration();
                configuration.load(file);
                clan = new Clan(configuration);
            } catch (InvalidConfigurationException | InvalidClanConfigurationException | IOException ignored) {}
        }

        if (clan != null) {
            boolean expire = true;

            for (ClanMember member : clan.getMembers()) {
                if (member.getPlayer().isOnline()) {
                    expire = false;
                    break;
                }
            }

            ClansPlugin.cacheClan(clan, expire);
        }

        return clan;
    }

    public static boolean clanExist(String name) {
        return getClan(name) != null;
    }

    public static boolean isValidClanName(@NotNull String name) {
        return name.matches("[a-zA-Z0-9]+") && name.length() > 3 && name.length() < 12;
    }
}