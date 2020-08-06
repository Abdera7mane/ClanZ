package me.ag.clans;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.ag.clans.commands.ClanCommand;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.listener.AsyncPlayerChatListener;
import me.ag.clans.listener.PlayerDamageListener;
import me.ag.clans.listener.PlayerJoinListener;
import me.ag.clans.listener.PlayerQuitListener;
import me.ag.clans.messages.Messages;
import me.ag.clans.types.Clan;

import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClansPlugin extends JavaPlugin {
    private static final long SAVE_INTERVAL = 30_000L;
    private static ClansPlugin instance;
    private static PluginManager pluginManager;
    private static Logger logger;

    private  Economy econ;
    private final MemoryCache<String, Clan> clansCache = new Cache<>(60_000L);
    private final MemoryCache<OfflinePlayer, PlayerConfiguration> playersCache = new Cache<>(60_000L);
    private Messages messages;
    private ClanCommand mainCommand;
    private int saveTaskID;

    class SaveTask extends Thread {

        @Override
        public void run() {
                this.saveClans();
                this.savePlayers();
        }
        protected void saveClans() {
            for (Clan clan : clansCache.values()) {
                try {
                    clan.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        protected void savePlayers() {
            for (PlayerConfiguration playerConfig: playersCache.values()) {
                try {
                    playerConfig.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        log("loading configuration");
        if (!loadConfig()) {
            log("configuration failed to load disabling plugin");
            getPluginManager().disablePlugin(this);
            return;
        };

        pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerDamageListener(), this);
        pluginManager.registerEvents(new AsyncPlayerChatListener(), this);

        this.mainCommand = new ClanCommand();
        this.registerCommand(this.getDescription().getName(), mainCommand);

        if (!this.setupEconomy()) {
            log("Vault not found! some functions are disabled", Level.WARNING);
        }

        new Metrics(this, 8293);


        this.saveTaskID = getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                new SaveTask(),
                0L,
                SAVE_INTERVAL
        );
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTask(saveTaskID);
        int cSize = 0;
        for (Clan clan : clansCache.values()) {
            try {
                clan.save();
                ++cSize;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log(cSize + "/" + clansCache.size() + " §aClans have been saved", Level.INFO);
        clearClansCache();

        int pSize = 0;
        for (PlayerConfiguration playerConfig : playersCache.values()) {
            try {
                playerConfig.save();
                ++pSize;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log(pSize + "/" + playersCache.size() + " §aPlayers have been saved", Level.INFO);
        clearPlayersCache();
    }

    public static ClansPlugin getInstance() {
        return instance;
    }

    private void registerCommand(String fallback, Command command) {
        try {
            Field bukkitCommandMap = this.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap)bukkitCommandMap.get(this.getServer());
            commandMap.register(fallback, command);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = rsp.getProvider();
                return econ != null;
            }
        }
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Economy getEconomy() {
        return econ;
    }

    public ClanCommand getMainCommand() {
        return this.mainCommand;
    }

    public boolean loadConfig() {
        File configFile = new File(this.getDataFolder(), File.separator + "config.yml");
        if (!configFile.isFile()) {
            this.saveResource("config.yml", false);
        }
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        Reader messagesRsc = getTextResource("messages.yml");
        if (messagesRsc == null) {
            log("couldn't find messages.yml in plugin's resource folder");
            return false;
        } else {
            try {
                this.messages = new Messages(messagesRsc);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        log("Configuration successfully loaded");
        return true;
    }

    public Messages getMessages() {
        return this.messages;
    }

    @Nullable
    public Clan getClanCache(@NotNull String name) {
        return clansCache.get(name.toLowerCase());
    }

    public  void cacheClan(Clan clan, boolean expire) {
        log(clan + "cached, expire=" + expire, ClansPlugin.LogLevel.DEBUG);
        clansCache.put(clan.getName().toLowerCase(), clan, expire);
    }

    public boolean isClanCached(@NotNull String name) {
        return clansCache.get(name.toLowerCase()) != null;
    }

    public void removeClanCache(@NotNull String name) {
        clansCache.remove(name.toLowerCase());
    }

    public void clearClansCache() {
        clansCache.clear();
    }

    @Nullable
    public PlayerConfiguration getPlayerCache(OfflinePlayer player) {
        return playersCache.get(player);
    }

    public void cachePlayer(PlayerConfiguration configuration, boolean expire) {
        log(configuration + "cached, expire=" + expire, ClansPlugin.LogLevel.DEBUG);
        playersCache.put(configuration.getPlayer(), configuration, expire);
    }

    public boolean isPlayerCached(OfflinePlayer player) {
        return playersCache.containsKey(player);
    }

    public void removePlayerCache(@NotNull OfflinePlayer player) {
        playersCache.remove(player);
    }

    public void clearPlayersCache() {
        playersCache.clear();
    }

    public static void log(String log) {
        log(log, Level.INFO);
    }

    public static void log(String log, Level logLevel) {
        logger.log(logLevel, String.join(" ", log));
    }

    static class LogLevel extends Level {
        public static final Level DEBUG = new ClansPlugin.LogLevel("DEBUG", 600);

        protected LogLevel(String name, int value) {
            super(name, value);
        }
    }
}