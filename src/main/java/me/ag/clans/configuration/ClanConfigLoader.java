package me.ag.clans.clanconfig;

import javax.annotation.Nullable;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import me.ag.clans.types.Clan;

import me.ag.clans.types.ClanMember;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;

public class ClanConfigLoader {

    @Nullable
    public static Clan load(String path) throws ParseException {
        return load(new File(path));
    }

    @Nullable
    public static Clan load(File file) throws ParseException {
        FileConfiguration clanConfig = ClanConfiguration.loadConfiguration(file);
        return load((ClanConfiguration) clanConfig);
    }

    public static Clan load(ClanConfiguration clanConfig) throws ParseException {
        Clan clan = null;
        if (parseConfig(clanConfig)) {
            String name = clanConfig.getName();
            UUID leaderUUID = UUID.fromString(clanConfig.getString("leader"));
            OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderUUID);
            Date creationDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(clanConfig.getString("creation-date"));
            clan = new Clan(
                    name,
                    leader,
                    creationDate,
                    clanConfig.getLong("level"),
                    clanConfig.getInt("total-kills"),
                    getMembersfromConfig(clanConfig)
            );
        }
        return clan;
    }

    public static boolean parseConfig(FileConfiguration config) {
        return config.contains("leader") &&
            config.contains("isOpen") &&
            config.contains("creation-date") &&
            config.contains("level") &&
            config.contains("total-kills") &&
            config.contains("members." + config.getString("leader", "*"));
    }

    @Nullable
    private static ClanMember[] getMembersfromConfig(FileConfiguration config) {
        List<?> members_list = config.getList("members");

        ClanMember[] members = new ClanMember[members_list.size()];
        return members_list.toArray(members);
    }
}
