package me.ag.clans.types;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.ClanConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClanMember {
    private  Clan clan;
    private OfflinePlayer player;
    private ClanRole role;
    private int totalKills;
    private Date joinDate;

    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ClanMember(Clan clan, OfflinePlayer player, ClanRole... role) {
        this.clan = clan;
        this.player = player;
        this.role = role.length > 0 ? role[0] : ClanRole.MEMBER;
        this.joinDate = new Date();

    }
    public ClanMember(Clan clan, ConfigurationSection configuration) {
        this.clan = clan;
        this.player = configuration.getOfflinePlayer("player");
        this.role = ClanRole.valueOf(configuration.getString("role", "member"));
        this.totalKills = configuration.getInt("total-kills", 0);
        try {
            this.joinDate = dateFormat.parse(configuration.getString("join-date"));
        } catch (ParseException e) {
            this.joinDate = new Date();
        }

    }

    public Clan getClan() {
        return this.clan;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public ClanRole getRole() {
        return this.role;
    }

    public Date getJoinDate() {
        return this.joinDate;
    }

    public int getKills() {
        return this.totalKills;
    }

    public void setKills(int kills) {
        this.totalKills = kills;
    }

    public void setRole(ClanRole role) {
        if (this.role == role) {
            return;
        }
        else if (role == ClanRole.LEADER) {
            clan.getLeader().demote();
        }

        this.role = role;
    }

    public void promote() {
        ClanRole[] roles = ClanRole.values();
        role = roles[Math.min(role.priority + 1, roles.length)];
    }

    public void demote() {
        ClanRole[] roles = ClanRole.values();
        role = roles[Math.max(role.priority - 1, roles.length)];
    }

    public void leaveClan() {
        ClanConfiguration clanConfiguration = clan.configuration();
        String uuid = this.player.getUniqueId().toString();
        clanConfiguration.set(String.format("members.%s", uuid), null);

        String fileName = uuid + ".yml";
        File file = new File(plugin.getDataFolder(), "\\data\\players\\" + fileName);
        FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
        playerConfiguration.set("clan", null);
        try {
            playerConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ClanMember) {
            return ((ClanMember) object).getPlayer() == this.player;
        }
        return false;
    }

//    public void save() {
//        final String uuid = this.player.getUniqueId().toString();
//        ClanConfiguration clanConfiguration = this.clan.configuration();
//        clanConfiguration.set(String.format("members.%s.role", uuid), this.role.getDisplayName());
//        clanConfiguration.set(String.format("members.%s.kills", uuid), this.totalKills);
//        clanConfiguration.set(String.format("members.%s.join-date", uuid), dateFormat.format(this.joinDate));
//
//        String fileName = uuid + ".yml";
//        File file = new File(plugin.getDataFolder(), "\\data\\players\\" + fileName);
//        FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
//        playerConfiguration.set("clan", this.clan.getName());
//        try {
//            playerConfiguration.save(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
