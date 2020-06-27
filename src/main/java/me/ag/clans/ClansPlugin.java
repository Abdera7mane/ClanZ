package me.ag.clans;

import java.io.File;
import java.text.ParseException;
import java.util.Map;
import java.util.HashMap;

import me.ag.clans.configuration.ClanConfigLoader;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import me.ag.clans.commands.ClanCommand;
import me.ag.clans.types.Clan;

import javax.annotation.Nullable;


public class ClansPlugin extends JavaPlugin {
    private static ClansPlugin instance;
    private Map<String, Clan> loadedClans = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
        Mechanics event = new Mechanics();
        this.getServer().getPluginManager().registerEvents(event, this);
        this.getCommand("clan").setExecutor(new ClanCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ClansPlugin getInstance() {
        return instance;
    }

    public boolean loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.getServer().getConsoleSender().sendMessage("§2Config loaded");
        this.getServer().getConsoleSender().sendMessage("ok " + Integer.toString((int) getConfig().get("max-name-length")));
        return true;
    }

    @Nullable
    public Clan getClan(String name) throws ParseException {
        return loadedClans.containsKey(name) ? loadedClans.get(name) : ClanConfigLoader.load(this.getDataFolder().getPath() + name + ".yml");
    }

    public boolean clanExists(String name) {
        return new File(this.getDataFolder().getPath() + name + ".yml").exists();
    }

    public boolean createClan(String name, OfflinePlayer leader) {
        if (clanExists(name)) return false;
        Clan clan = new Clan(name, leader);
        clan.save();
        loadedClans.put(name, clan);
        leader.getPlayer().sendMessage("§ayou have created a new clan named §7" + name + "§a !");
        return true;
    }

    public Clan[] getLoadedClans() {
        return loadedClans.values().toArray(new Clan[loadedClans.size()]);
    }

    public void loadClan(Clan clan) {
        this.loadedClans.put(clan.getName(), clan);
    }
    public void unloadAll() {
        this.loadedClans.clear();
    }

    public void unloadClan(String clan) {
        loadedClans.remove(clan);
    }
}
