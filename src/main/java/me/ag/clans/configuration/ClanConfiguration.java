package me.ag.clans.configuration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;
import me.ag.clans.types.Clan.Status;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class ClanConfiguration extends YamlConfiguration {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    public static final File defaultPath;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        String childPath = "\\data\\clans".replace("\\", File.separator);
        defaultPath = new File(plugin.getDataFolder(), childPath);
    }

    public static ClanConfiguration fromClan(@NotNull Clan clan) {
        ClanConfiguration configuration = new ClanConfiguration();
        configuration.set("name", clan.getName());
        configuration.set("leader", clan.getLeader().getPlayer().getUniqueId().toString());
        configuration.set("description", clan.getDescription());
        configuration.set("creation-date", dateFormat.format(clan.getCreationDate()));
        configuration.set("status", clan.getStatus().toString());
        configuration.set("total-kills", clan.getKills());
        configuration.set("level", clan.getLevel());

        for (ClanMember member : clan.getMembers()) {
            String uuid = member.getPlayer().getUniqueId().toString();
            configuration.createSection("members." + uuid, member.toConfiguration().getValues(true));
        }

        return configuration;
    }

    public static boolean validate(@NotNull FileConfiguration configuration) {
        return configuration.isString("name") && configuration.contains("leader") && configuration.isConfigurationSection("members");
    }

    public Status getStatus() {
        return Status.valueOf(this.getString("status"));
    }

    public Date getCreationDate() {
        Date date = new Date();
        if (this.isString("creation-date")) {
            try {
                date = dateFormat.parse(this.getString("creation-date"));
            } catch (ParseException var3) {
                date = new Date();
            }
        }

        return date;
    }

    public String getDisplayName() {
        return this.getString("name", "");
    }

    public String getDescription() {
        return this.getString("description", "");
    }

    public int getKills() {
        return this.getInt("total-kills", 0);
    }

    @Nullable
    public OfflinePlayer getLeader() {
        String uuid = this.getString("leader");
        return uuid == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    public long getLevel() {
        return this.getLong("level", 0L);
    }

    public Set<ClanMemberConfigurationSection> getMembers() {
        Set<ClanMemberConfigurationSection> members = new HashSet<>();
        if (this.isConfigurationSection("members")) {
            ConfigurationSection mainSection = this.getConfigurationSection("members");
            for (String uuid : mainSection.getKeys(false)) {
                ConfigurationSection subSection = mainSection.getConfigurationSection(uuid);
                if (subSection != null) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    ClanMemberConfigurationSection configSection = new ClanMemberConfigurationSection(player);
                    configSection.addDefaults(subSection.getValues(true));
                    members.add(configSection);
                }
            }
        }

        return members;
    }

    public void save() throws IOException {
        String childPath = "\\".replace("\\", File.separator) + this.getDisplayName().toLowerCase() + ".yml";
        File dataFile = new File(defaultPath, childPath);
        this.save(dataFile);
    }
}
