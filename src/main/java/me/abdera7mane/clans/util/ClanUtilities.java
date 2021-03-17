package me.abdera7mane.clans.util;


import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.ClansPlugin;

import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanUtilities {

    public static boolean createClan(@NotNull String name, @NotNull OfflinePlayer leader) {
        return false;
    }

    @Nullable
    public static Clan getClan(String name) {
        return getPlugin().getStorageService().getClan(name);
    }

    public static boolean clanExists(String name) {
        return getClan(name) != null;
    }

    public static boolean isValidClanName(@NotNull String name) {
        return name.matches("^\\w+$") && name.length() > 3 && name.length() < 12;
    }

    private static ClansPlugin getPlugin() {
        ClansPlugin plugin = ClansPlugin.getInstance();
        if (plugin == null) {
            throw new RuntimeException("Plugin not initiated");
        }
        return plugin;
    }

    public static void saveClan(Clan clan) {

    }
}