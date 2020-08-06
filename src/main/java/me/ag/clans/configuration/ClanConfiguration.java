package me.ag.clans.configuration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanConfiguration extends YamlConfiguration {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    public static final File defaultPath;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        String childPath = "\\data\\clans".replace("\\", File.separator);
        defaultPath = new File(plugin.getDataFolder(), childPath);
    }

    /**
     * Returns a <code>ClanConfiguration</code> based on a given Clan
     * @param clan Clan object which is going to be converted to ClanConfiguration
     * @return a ClanConfiguration and can't return null
     */
    @NotNull
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

    /**
     * tells whether a FileConfiguration is a valid ClanConfiguration
     * @param configuration the configuration object which is going to be validated
     * @return true if the configuration is valid, false otherwise
     */
    public static boolean validate(@NotNull FileConfiguration configuration) {
        return configuration.isString("name") && configuration.contains("leader") && configuration.isConfigurationSection("members");
    }

    /**
     * Gets clan's status
     * @return Status value
     */
    @NotNull
    public Clan.Status getStatus() {
        return Clan.Status.valueOf(this.getString("status", "PUBLIC"));
    }

    /**
     * Gets the creation date of a clan
     * @return the actual creation <code>Date</code> if found, else it will return a new <code>Date</code>
     */
    @NotNull
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

    /**
     * Gets the display name of a clan from its configuration.
     * You must ensure that you're dealing with a validConfiguration before calling this method
     * @return clan name, by default returns an empty string
     */
    @NotNull
    public String getDisplayName() {
        return this.getString("name", "");
    }

    /**
     * Gets the clan's description
     * @return description, empty String by default
     */
    @NotNull
    public String getDescription() {
        return this.getString("description", "");
    }

    /**
     * Gets the total kills of the clan
     * @return total kills, 0 by default
     */
    public int getKills() {
        return this.getInt("total-kills", 0);
    }

    /**
     * Gets the leader of the clan
     * if it returned null, it means that <b>the configuration is invalid </b>
     * @return an <code>OfflinePlayer</code>, null if the configuration doesn't contain 'leader' key
     */
    @Nullable
    public OfflinePlayer getLeader() {
        String uuid = this.getString("leader");
        return uuid == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    /**
     * Gets the clan's level
     * @return level of the clan, 0 by default
     */
    public long getLevel() {
        return this.getLong("level", 0L);
    }

    /**
     * Gets a Set of {@link ClanMemberConfigurationSection}
     * @return all clan members configuration
     */
    public Set<ClanMemberConfigurationSection> getMembers() {
        Set<ClanMemberConfigurationSection> members = new HashSet<>();
        if (this.isConfigurationSection("members")) {
            ConfigurationSection mainSection = this.getConfigurationSection("members");
            assert mainSection != null; //again just to get rid of the warning
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

    /**
     * Save the configuration at {@link #defaultPath}
     * @throws IOException thrown an I/O exception occur while saving the configuration file
     */
    public void save() throws IOException {
        String childPath = File.separator + this.getDisplayName().toLowerCase() + ".yml";
        File dataFile = new File(defaultPath, childPath);
        this.save(dataFile);
    }
}
