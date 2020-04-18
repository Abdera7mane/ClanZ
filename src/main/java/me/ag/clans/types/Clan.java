package me.ag.clans.types;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


import org.bukkit.OfflinePlayer;

import me.ag.clans.Clans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Clan {
    private Clans plugin = Clans.getPlugin(Clans.class);

    private String name;

    public String description;
    public boolean isOpen;
    public short level;
    public int totalKills;
    private Date creationDate;
    private ClanMember leader;
    private Map<OfflinePlayer, ClanMember> members = new HashMap<>();


    public Clan(String name, OfflinePlayer leader) {
        this.name = name;
        this.addMember(leader);
        this.setLeader(leader);

    }

    public String getName() {
        return name;
    }

    public void addMember(OfflinePlayer player) {
        ClanMember member = new ClanMember(player);
        members.put(player, member);
    }
    public void removeMember(OfflinePlayer player) {
        if (members.containsKey(player))
            members.get(player).leaveClan();
    }

    public void setLeader(OfflinePlayer leader) {
        if (this.leader != null) this.leader.role = ClanRole.CO_LEADER;
        this.leader = new ClanMember(leader, ClanRole.LEADER);
    }

    public ClanMember getLeader() {
        return this.leader;
    }

    public ClanMember[] getMembers() {
        int size = members.size();
        ClanMember[] players = new ClanMember[size];
        for (int i = 0; i < size; i++)
            players[i] = members.get(i);
        return players;
    }

    public void delete() {
        for (ClanMember member : members.values()) {
            member.leaveClan();
        }
    }

    public void save() {
        final String fileName = this.name + ".yml";
        try {
            File dataFile = new File(plugin.getDataFolder(), "\\data\\" + fileName);
            FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

            data.set("description", this.description);
            data.set("is-open", this.isOpen);
            data.set("creation-date", this.creationDate.toString());
            data.set("level", this.level);
            data.set("total-kills", this.totalKills);
            data.set("leader", this.getLeader().getPlayer().getUniqueId().toString());
            data.save(dataFile);

        } catch (IOException e) {
            System.out.println("An error occured while creating the file '" + fileName + "'");
            e.printStackTrace();
            return;
        }
    }

}
