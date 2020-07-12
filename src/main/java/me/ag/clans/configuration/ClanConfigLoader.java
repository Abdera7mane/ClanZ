package me.ag.clans.configuration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

import me.ag.clans.types.Clan;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;

@Deprecated
public class ClanConfigLoader {

    @Nullable
    public static Clan load(File file) {
        Validate.notNull(file, "File can't be null");
        if (!file.isFile()) return null;
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        ClanConfiguration clanConfig = new ClanConfiguration();
        try {
            clanConfig.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        boolean valid = ClanConfiguration.validate(clanConfig);
        return valid ? new Clan(fileName, clanConfig) : null;
    }

//    public static Clan load(ClanConfiguration clanConfig) throws ParseException {
//        Clan clan = null;
//        if (parseConfig(clanConfig)) {
//            String name = clanConfig.getName();
//            UUID leaderUUID = UUID.fromString(clanConfig.getString("leader"));
//            OfflinePlayer leader = Bukkit.getOfflinePlayer(leaderUUID);
//            Date creationDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(clanConfig.getString("creation-date"));
//            clan = new Clan(
//                    name,
//                    leader,
//                    creationDate,
//                    clanConfig.getLong("level"),
//                    clanConfig.getInt("total-kills"),
//                    getMembersfromConfig(clanConfig)
//            );
//        }
//        return clan;
//    }

}
