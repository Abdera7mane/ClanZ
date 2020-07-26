package me.ag.clans.types;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.ag.clans.ClansPlugin;
import me.ag.clans.configuration.ClanConfiguration;
import me.ag.clans.configuration.ClanMemberConfigurationSection;
import me.ag.clans.configuration.InvalidClanConfigurationException;
import me.ag.clans.configuration.PlayerConfiguration;
import me.ag.clans.events.ClanDeleteEvent;
import me.ag.clans.events.PlayerJoinClanEvent;
import me.ag.clans.events.PlayerLeaveClanEvent;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Clan {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private final String name;
    private String description = "";
    private Status state = Status.PUBLIC;
    private long level;
    private int totalKills;
    private final Date creationDate;
    private ClanMember leader;
    private final Map<OfflinePlayer, ClanMember> members = new HashMap<>();

    public enum Status {
        PUBLIC,
        INVITE_ONLY,
        CLOSED

    }

    public enum LeaveReason {
        QUIT,
        KICK

    }

    public Clan(@NotNull String name, @NotNull OfflinePlayer leader) {
        this.name = name;
        this.addMember(leader, true);
        this.setLeader(leader);
        this.creationDate = new Date();
    }

    public Clan(@NotNull ClanConfiguration configuration) throws InvalidClanConfigurationException {
        if (!ClanConfiguration.validate(configuration)) {
            throw new InvalidClanConfigurationException(configuration + " has missing keys values: 'name', 'leader' or 'members' configuration");
        }

        this.name = configuration.getDisplayName();
        this.setDescription(configuration.getDescription());
        this.creationDate = configuration.getCreationDate();
        this.setKills(configuration.getKills());
        this.setLevel(configuration.getLevel());
        this.setStatus(configuration.getStatus());

        for (ClanMemberConfigurationSection memberConfig : configuration.getMembers()) {
            ClanMember member = new ClanMember(this, memberConfig);
            this.members.put(member.getPlayer(), member);
        }

        this.setLeader(configuration.getLeader());

    }

    public void addMember(@NotNull OfflinePlayer player, boolean silent) {
        if (!this.hasMember(player)) {
            PlayerJoinClanEvent event = new PlayerJoinClanEvent(player, this);
            plugin.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                ClanMember member = new ClanMember(this, player, ClanRole.MEMBER);
                this.members.put(player, member);

                PlayerConfiguration playerConfig = PlayerUtilities.getPlayerConfiguration(player);
                playerConfig.setClan(this.name);
                playerConfig.removeInvitation(this);

                try {
                    playerConfig.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!silent) {
                    member.sendMessage("§aYou have joined §r" + this.getName() + " §aclan.");
                }
            }

        }
    }

    public void removeMember(@NotNull OfflinePlayer player, @NotNull Clan.LeaveReason reason) {
        PlayerLeaveClanEvent event = new PlayerLeaveClanEvent(player, this, reason);
        if (!event.isCancelled() && this.members.containsKey(player)) {
            this.members.get(player).leaveClan(false);
        }

        this.members.remove(player);
    }

    public void setLeader(@NotNull OfflinePlayer leader) {
        if (this.hasMember(leader)) {
            if (this.leader != null) {
                if (this.leader.getPlayer() == leader) {
                    return;
                }

                this.leader.demote();
            }

            ClanMember newLeader = this.members.get(leader);
            newLeader.setRole(ClanRole.LEADER);
            this.leader = newLeader;
        }

    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public ClanMember getLeader() {
        return this.leader;
    }

    public int getKills() {
        return this.totalKills;
    }

    public long getLevel() {
        return this.level;
    }

    @Nullable
    public ClanMember getMember(OfflinePlayer player) {
        return this.members.get(player);
    }

    public boolean hasMember(OfflinePlayer player) {
        return this.members.containsKey(player);
    }

    public ClanMember[] getMembers() {
        return this.members.values().toArray(new ClanMember[0]);
    }

    public Clan.Status getStatus() {
        return this.state;
    }

    public void setStatus(@NotNull Clan.Status newState) {
        this.state = newState;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public void setKills(int kills) {
        this.totalKills = Math.max(kills, 0);
    }

    public void setLevel(long level) {
        this.level = Math.max(level, 0L);
    }

    public void sendMessage(String message) {
        for (ClanMember member : this.members.values()) {
            member.sendMessage(message);
        }
    }

    public void broadcast(String message, Player broadcaster) {
        sendMessage("[" + this.name +  "]" + message);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Clan && ((Clan) object).getName().equals(this.name);
    }

    public void save() throws IOException {
        ClanConfiguration configuration = ClanConfiguration.fromClan(this);
        configuration.save();
    }

    public void delete() {
        ClanDeleteEvent event = new ClanDeleteEvent(this);
        plugin.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {

            for (ClanMember member : members.values()) {
                this.removeMember(member.getPlayer(), Clan.LeaveReason.KICK);
            }

            String childPath = File.separator + this.name.toLowerCase() + ".yml";
            File file = new File(ClanConfiguration.defaultPath, childPath);
            file.delete();
            ClansPlugin.removeClanCache(this.name);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "[" + "name='"
                + this.name
                + "', leader="
                + this.leader.getPlayer().getUniqueId()
                + ", members.size="
                + this.members.size() + "]";
    }


}
