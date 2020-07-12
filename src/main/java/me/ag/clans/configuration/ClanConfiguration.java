package me.ag.clans.configuration;

import me.ag.clans.types.Clan;

import me.ag.clans.types.ClanMember;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClanConfiguration extends YamlConfiguration {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static ClanConfiguration fromClan(Clan clan) {
        ClanConfiguration configuration = new ClanConfiguration();
        configuration.set("leader", clan.getLeader().getPlayer().getUniqueId().toString());
        configuration.set("description", clan.getDescription());
        configuration.set("creation-date", dateFormat.format(clan.getCreationDate()));
        configuration.set("is-open", clan.isOpen());
        configuration.set("total-kills", clan.getKills());
        for (ClanMember member : clan.getMembers()) {
            String uuid = member.getPlayer().getUniqueId().toString();
            String joinDate = dateFormat.format(member.getJoinDate());
            configuration.set(String.format("members.%s.join-date", uuid), joinDate);
            configuration.set(String.format("members.%s.role", uuid), member.getRole().toString());
            configuration.set(String.format("members.%s.kills", uuid), member.getKills());
        }

        return configuration;
    }

    public static boolean validate(FileConfiguration configuration) {
        return configuration.getOfflinePlayer("leader") != null && configuration.isList("members");
    }

    public boolean isOpen() {
        return getBoolean("is-open", true);
    }

    public Date getCreationDate() {
        Date date;
        try {
            date = dateFormat.parse(getString("creation-date"));
        } catch (ParseException e) {
            date = new Date();
        }
        return date;
    }

    public String getDescription() {
        return getString("description", "");
    }

    public int getKills() {
        return getInt("total-kills", 0);
    }

    @Nullable
    public OfflinePlayer getLeader() {
        return getOfflinePlayer("leader");
    }

    public long getLevel() {
        return getLong("level", 0);
    }

    public List<ConfigurationSection> getMembers() {
        List<ConfigurationSection> members = new ArrayList<>();
        for (String uuid : getStringList("members")) {
            ConfigurationSection configSection = getConfigurationSection("members." + uuid);
            if (configSection != null) {
                configSection.set("player", uuid);
                members.add(configSection);
            }
        }
        return members;
    }
}
