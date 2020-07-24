package me.ag.clans.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanInvitation;
import me.ag.clans.util.ClanUtilities;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class PlayerConfiguration extends YamlConfiguration {
    private final OfflinePlayer player;
    public static final File defaultPath;
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    static {
        String childPath = "\\data\\players".replace("\\", File.separator);
        defaultPath = new File(plugin.getDataFolder(), childPath);
    }

    public PlayerConfiguration(OfflinePlayer player) {
        this.player = player;
    }

    @Nullable
    public Clan getClan() {
        Clan clan = null;
        if (this.isString("clan")) {
            String clanName = this.getString("clan", "");
            clan = ClanUtilities.getClan(clanName);
        }

        if (clan == null) {
            this.setClan(null);
        }

        return clan;
    }

    public void setClan(String name) {
        this.set("clan", name);
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public Set<ClanInvitation> getInvitations() {
        Set<ClanInvitation> invites = new HashSet<>();

        for(String clanName : this.getStringList("invites")) {
            Clan instance = ClanUtilities.getClan(clanName);
            if (instance != null) {
                ClanInvitation invitation = new ClanInvitation(instance, this.player);
                invites.add(invitation);
            }
        }

        this.setInvitations(invites);
        return invites;
    }

    public void setInvitations(@NotNull Set<ClanInvitation> invites) {
        Set<String> list = new HashSet<>();

        for (ClanInvitation invitation : invites) {
            list.add(invitation.getClan().getName().toLowerCase());
        }

        this.set("invites", list.size() > 0 ? new ArrayList<>(list) : null);
    }

    public void addInvitation(@NotNull ClanInvitation invitation) {
        Set<ClanInvitation> invitations = this.getInvitations();
        invitations.add(invitation);
        this.setInvitations(invitations);
    }

    public void removeInvitation(@NotNull Clan from) {
        Set<ClanInvitation> list = this.getInvitations();
        list.remove(new ClanInvitation(from, this.player));
        this.setInvitations(list);
    }

    public boolean isInvitedBy(@NotNull Clan clan) {
        return this.getStringList("invites").contains(clan.getName().toLowerCase());
    }

    public void save() throws IOException {
        String uuid = this.player.getUniqueId().toString();
        String childPath = uuid + ".yml";
        File file = new File(defaultPath, childPath);
        this.save(file);
    }

    @Override
    public void save(@NotNull File file) throws IOException {
        if (this.getKeys(false).size() > 0) {
            super.save(file);
        } else {
            file.delete();
        }

    }

    @Override
    public String toString() {
        Configuration root = this.getRoot();
        return this.getClass().getSimpleName()
                + "[path='" + this.getCurrentPath()
                + "', root='" + (root == null ? null : root.getClass().getSimpleName())
                + "', player=" + this.player.getUniqueId() + "]";
    }
}