package me.ag.clans;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import me.ag.clans.configuration.PlayerConfiguration;
import net.milkbowl.vault.economy.Economy;

//import org.bstats.bukkit.Metrics;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.ag.clans.commands.ClanCommand;
import me.ag.clans.types.Clan;
import me.ag.clans.listener.PlayerJoinListener;

public class ClansPlugin extends JavaPlugin {
    private static ClansPlugin instance;
    private static Economy econ;
    private static MemoryCache<String, Clan> clansCache = new Cache<>(60000);
    private static MemoryCache<OfflinePlayer, PlayerConfiguration> playersCache = new Cache<>(60000);
    private static PluginManager pluginManager;
    private static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);

        log("loading configuration");
        loadConfig();
        saveResource("messages.yml", false);

        registerCommand(this.getDescription().getName(), new ClanCommand());

        if (!setupEconomy() ) {
            log("Vault not found! some functions are disabled");
        }
        //Metrics metrics = new Metrics(this, 516231);
    }

    @Override
    public void onDisable() {
        int size = clansCache.size();
        for (Clan clan : clansCache.values()) {
            clan.save();
        }
        log(size + " §aClans have been saved");
        clearClansCache();
    }

    public static ClansPlugin getInstance() {
        return instance;
    }

    private void registerCommand(String fallback, Command command) {
        try {
            Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(getServer());
            commandMap.register(fallback, command);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static Clan getClanCache(String name) {
        return clansCache.get(name);
    }

    public static void cacheClan(Clan clan) {
        cacheClan(clan, true);
    }

    public static void cacheClan(Clan clan, boolean expire) {
        clansCache.put(clan.getName(), clan, expire);
    }

    public static boolean isClanCached(String name) {
        return clansCache.containsKey(name);
    }

    public static void clearClansCache() {
        clansCache.clear();
    }


    public static PlayerConfiguration getPlayerCache(OfflinePlayer player) {
        return playersCache.get(player);
    }

    public static void cachePlayer(PlayerConfiguration configuration) {
        cachePlayer(configuration, true);
    }

    public static void cachePlayer(PlayerConfiguration configuration, boolean expire) {
        playersCache.put(configuration.getPlayer(), configuration, expire);
    }

    public static boolean isPlayerCached(OfflinePlayer player) {
        return playersCache.containsKey(player);
    }

    public static void clearPlayersCache() {
        playersCache.clear();
    }

    public static void log(String... log) {
        logger.info(String.join(" ", log));
    }

    public static void warn(String... log) {
        logger.warning(String.join("", log));
    }

}
