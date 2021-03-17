package me.ag.clans.util;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;

import me.ag.clans.types.PlayerAdapter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUtilities {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    @Nullable
    public static Clan getPlayerClan(OfflinePlayer player) {
//        PlayerConfiguration configuration = getPlayerConfiguration(player);
        return null;
    }

    @NotNull
    public static PlayerAdapter getPlayerConfiguration(@NotNull OfflinePlayer player) {
//        PlayerConfiguration configuration = new PlayerConfiguration(player);
//        if (plugin.isPlayerCached(player)) {
//            configuration = plugin.getPlayerCache(player);
//        } else {
//            try {
//                String uuid = player.getUniqueId().toString();
//                String childPath = File.separator + uuid + ".yml";
//                File file = new File(PlayerConfiguration.defaultPath, childPath);
//                configuration.load(file);
//                if (configuration.getKeys(false).size() == 0) {
//                    file.delete();
//                }
//            } catch (InvalidConfigurationException | IOException ignored) {}
//        }
//
//        plugin.cachePlayer(configuration, !player.isOnline());
        return null;
    }

    public static boolean hasClan(@NotNull OfflinePlayer player) {
        return getPlayerClan(player) != null;
    }

    public static boolean getChatMetaDataValue(Player player) {
        boolean value = false;
        for (MetadataValue meta : player.getMetadata("chat")) {
            if (meta.getOwningPlugin() == plugin) {
                value = meta.asBoolean();
            }
        }

        return value;
    }

    public static void setChatMetaDataValue(Player player, boolean value) {
        player.setMetadata("chat", new FixedMetadataValue(plugin, true));
    }
}
