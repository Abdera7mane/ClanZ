package me.abdera7mane.clans;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import me.abdera7mane.clans.commands.ClanCommand;
import me.abdera7mane.clans.commands.PluginCommandBuilder;
import me.abdera7mane.clans.events.ClanCreateEvent;
import me.abdera7mane.clans.listeners.PlayerEventsListener;
import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.placeholderapi.expansions.ClanZExpansion;
import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.types.PlayerAdapter;
import me.abdera7mane.clans.util.LoadResult;
import me.abdera7mane.clans.util.StorageService.CacheStorageService;
import me.abdera7mane.clans.util.StorageService.file.yaml.YAMLStorage;

import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClansPlugin extends JavaPlugin implements ClanZAPI {
    private static final long SAVE_INTERVAL = 3_000L;
    private static ClansPlugin instance;

    private CacheStorageService storageService;
    private Messages messages;
    private Command mainCommand;
    private int saveTaskID;
    private boolean usePAPI;
    private Economy econ;

    @Override
    public Clan createClan(@NotNull String name, @NotNull OfflinePlayer leader) {
        String simpleName = name.trim();

        if (this.clanExists(simpleName)) {
            return null;
        }

        ClanCreateEvent event = new ClanCreateEvent(simpleName, leader);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        Clan clan = new Clan(simpleName, leader);
        this.getStorageService().storeClan(clan);
        return clan;
    }

    @Nullable
    @Override
    public Clan getClan(@NotNull String name) {
        return this.getStorageService().getClan(name);
    }

    @NotNull
    @Override
    public PlayerAdapter getPlayerAdapter(@NotNull OfflinePlayer player) {
        PlayerAdapter adapter = this.getStorageService().getPlayerAdapter(player);
        if (adapter == null) {
            adapter = new PlayerAdapter(player);
            this.savePlayerAdapter(adapter);
        }
        return adapter;
    }

    @Override
    public void saveClan(@NotNull Clan clan) {
        this.getStorageService().storeClan(clan);
    }

    @Override
    public void savePlayerAdapter(@NotNull PlayerAdapter adapter) {
        this.getStorageService().storePlayerAdapter(adapter);
    }

    @Override
    public void onEnable() {
        instance = this;

        PluginManager pluginManager = this.getServer().getPluginManager();

        if (!this.setup()) {
            pluginManager.disablePlugin(this);
        }

        this.storageService = new YAMLStorage(this);

        this.registerListeners();
        this.registerCommands();
        this.installPapiExpansion();

        if (!this.setupEconomy()) {
            log("Vault not found! some functions are disabled", Level.WARNING);
        }


        this.saveTaskID = this.getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                () -> this.getStorageService().saveAll(),
                0L,
                SAVE_INTERVAL
        );

        if (this.saveTaskID == -1) {
            log("Failed scheduling SaveTask", Level.SEVERE);
            pluginManager.disablePlugin(this);
        }
        new Metrics(this, 8293);
    }

    @Override
    public void onDisable() {
        instance = null;

        this.getServer().getScheduler().cancelTask(saveTaskID);

        this.usePAPI = false;

        this.getStorageService().saveAll();
    }

    public Economy getEconomy() {
        return econ;
    }

    public Command getMainCommand() {
        return this.mainCommand;
    }

    public Messages getMessages() {
        return this.messages;
    }

    public CacheStorageService getStorageService() {
        return this.storageService;
    }

    public void log(String log) {
        this.log(log, Level.INFO);
    }

    public void log(String log, Level logLevel) {
        this.getLogger().log(logLevel, String.join(" ", log));
    }

    public boolean usePAPI() {
        return this.usePAPI;
    }

    public LoadResult loadConfigs() {
        LoadResult result = new LoadResult();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        Reader messagesResource = this.getTextResource("messages.yml");
        if (messagesResource == null) {
            result.appendError("couldn't load 'messages.yml' from plugin's resources");
            return result;
        }

        File messagesFile = new File(this.getDataFolder(), "messages.yml");

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(messagesResource);
        FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(messagesFile);

        if (!messagesFile.exists()) {
            try {
                defaultConfig.save(messagesFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String key : defaultConfig.getKeys(true)) {
            if (!currentConfig.contains(key)) {
                currentConfig.set(key, defaultConfig.get(key));
            }
        }

        ConfigurationSection soundsSection = currentConfig.getConfigurationSection("sounds");
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                String soundName = soundsSection.getString(key, "");
                try {
                    Sound.valueOf(soundName);
                } catch (IllegalArgumentException e) {
                    result.appendWarrning("sounds."+ key + ": '" + soundName + "' is an invalid sound name");
                }
            }
        }

        this.messages = new Messages(currentConfig);

        try {
            currentConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean setup() {
        boolean success = false;
        this.log("loading configuration");
        LoadResult result = this.loadConfigs();
        List<String> warnnings = result.getWarnnings();
        List<String> errors = result.getErrors();
        if (result.succeeded()) {
            if (warnnings.size() > 0) {
                warnnings.forEach(warnning -> this.log(warnning, Level.WARNING));
                this.log(String.format("§aConfiguration loaded§6 with %d warnnings", warnnings.size()));
            } else
                this.log("§aConfiguration successfully loaded");
            success = true;
        } else {
            warnnings.forEach(warnning -> this.log(warnning, Level.WARNING));
            errors.forEach(warnning -> this.log(warnning, Level.SEVERE));
            this.log(String.format("Configuration failed to load with %d errors and %d warnning", errors.size(), warnnings.size()), Level.SEVERE);
            this.log("Disabling plugin...");
        }
        return success;
    }

    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerEventsListener(this), this);
    }

    private void registerCommands() {
        ClanCommand command = new ClanCommand(this);
        PluginCommandBuilder builder = new PluginCommandBuilder("clan", this)
                .description("The main Clans command")
                .setAliases(Arrays.asList("clanz", "clans"))
                .withPermission("clanz.command")
                .commandExecutor(command)
                .tabCompleter(command);

        if (!builder.register()) {
            log("couldn't register '" + this.mainCommand.getLabel() + "' command");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.mainCommand = this.getServer().getPluginCommand("clan");
    }

    @SuppressWarnings("ConstantConditions")
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

    private void installPapiExpansion() {
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.usePAPI = new ClanZExpansion(this).register();
        }
    }

    public static ClansPlugin getInstance() {
        return instance;
    }
}