package me.ag.clans.configuration;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanInvite;
import me.ag.clans.util.ClanUtilities;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class PlayerConfiguration extends YamlConfiguration {
    private OfflinePlayer player;
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    @Override
    public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
        super.load(file);
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        UUID uuid = UUID.fromString(fileName);
        player = Bukkit.getOfflinePlayer(uuid);
    }

    public void addInvite(@NotNull Clan clan) {
        List<String> invites = getStringList("invites");
        invites.add(clan.getName());
        set("invites", invites);
    }


    @Nullable
    public Clan getClan() {
        String clan = getString("clan");
        return clan != null ? ClanUtilities.getClan(clan) : null;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public List<ClanInvite> getInvites() {
        List<ClanInvite> invites = new ArrayList<>();
        for (String clan : getStringList("invites")) {
            invites.add(new ClanInvite(ClanUtilities.getClan(clan), this.player));
        }
        return invites;
    }

    public void save() throws IOException {
        String uuid = this.player.getUniqueId().toString();
        String childPath = "\\data\\players\\".replace("\\", File.separator) + uuid + ".yml";
        File file = new File(plugin.getDataFolder(), childPath);
        save(file);
    }

    @Override
    public void save(@NotNull File file) throws IOException {
        if (this.saveToString().length() > 0) {
            super.save(file);
        }
    }
}
