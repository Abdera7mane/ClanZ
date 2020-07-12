package me.ag.clans.types;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.ClanConfiguration;
import me.ag.clans.events.ClanDeleteEvent;
import me.ag.clans.events.PlayerJoinClanEvent;
import me.ag.clans.events.PlayerLeaveClanEvent;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class Clan {
    private static ClansPlugin plugin = ClansPlugin.getInstance();

    private final String name;
    private String description = "";
    private boolean isOpen = true;
    private long level;
    private int totalKills;
    private Date creationDate;
    private ClanMember leader;
    private Map<OfflinePlayer, ClanMember> members = new HashMap<>();

    private ClanConfiguration configuration;

    public Clan(String name, OfflinePlayer leader) {
        this.name = name;
        this.addMember(leader);
        this.setLeader(leader);
        this.creationDate = new Date();
        configuration = new ClanConfiguration();
    }

    public Clan(String name, ClanConfiguration configuration) {
        this.name = name;
        this.description = configuration.getDescription();
        this.creationDate = configuration.getCreationDate();
        this.totalKills = configuration.getKills();
        this.level = configuration.getLevel();
        this.isOpen = configuration.isOpen();
        OfflinePlayer leader = configuration.getLeader();
        this.addMember(leader);
        this.setLeader(leader);
        for (ConfigurationSection memberConfig : configuration.getMembers()) {
            ClanMember member = new ClanMember(this, memberConfig);
            members.put(member.getPlayer(), member);
        }
    }

    public enum LeaveReason {
        QUIT,
        KICK,
    }

    public void addMember(OfflinePlayer player) {
        ClanMember member = new ClanMember(this, player);
        PlayerJoinClanEvent event = new PlayerJoinClanEvent(member.getPlayer(), this);
        plugin.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            members.put(player, member);
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null) {
                onlinePlayer.sendMessage("§aYou have joined §r" + this.getName() + " §aclan.");
            }
        }

    }

    public void removeMember(OfflinePlayer player, LeaveReason reason) {
        PlayerLeaveClanEvent event = new PlayerLeaveClanEvent(player, this, reason);
        if (!event.isCancelled() && members.containsKey(player))
            members.get(player).leaveClan();
            members.remove(player);
    }

    public void setLeader(OfflinePlayer leader) {
        if (this.leader != null) this.leader.setRole(ClanRole.CO_LEADER);
        this.leader = new ClanMember(this, leader, ClanRole.LEADER);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public ClanMember getLeader() {
        return this.leader;
    }

    public int getKills() {
        return this.totalKills;
    }

    public long getLevel() {
        return this.level;
    }

    @Nullable
    public ClanMember getMember(OfflinePlayer player) {
        return members.getOrDefault(player, null);
    }

    public ClanMember[] getMembers() {
        return members.values().toArray(new ClanMember[0]);
    }


    public boolean isOpen() {
        return this.isOpen;
    }

    public void open(boolean value) {
        this.isOpen = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKills(int kills) {
        this.totalKills = kills;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public ClanConfiguration configuration() {
        return configuration;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Clan) {
            return ((Clan) object).getName().equals(this.name);
        }
        return false;
    }

    public void save() {
        configuration = ClanConfiguration.fromClan(this);
        String childPath = "\\data\\clans\\".replace("\\", File.separator) + this.name + ".yml";
        File dataFile = new File(plugin.getDataFolder(), childPath);
        try {
            configuration.save(dataFile);

        } catch (IOException e) {
            ClansPlugin.log("An error occured while saving the file '" + dataFile.getName() + "'");
            e.printStackTrace();
        }
    }

    public void delete() {
        ClanDeleteEvent event = new ClanDeleteEvent(this);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        for (ClanMember member : members.values()) {
            member.leaveClan();
        }
        members.clear();
    }
}
