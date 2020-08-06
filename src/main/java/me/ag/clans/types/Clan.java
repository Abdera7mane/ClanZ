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
import org.bukkit.configuration.file.FileConfiguration;
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

    /**
     * Represents the possible clan's states
     * which determine if a player can join the clan or not
     */
    public enum Status {
        /**
         * All players are allowed to join
         */
        PUBLIC,

        /**
         * The player needs to send a join request
         * which will be approved by one of the clan's who meet certain permissions
         */
        INVITE_ONLY,

        /**
         * No one can join the clan unless they have received an invitation
         */
        CLOSED

    }

    public enum LeaveReason {
        QUIT,
        KICK

    }

    /**
     * Constructor which creates a new <code>Clan</code> instance
     * from a defined <i>name</i> and a <i>leader</i> and takes default clan configuration
     * @param name name of the clan
     * @param leader owner of the clan
     */
    public Clan(@NotNull String name, @NotNull OfflinePlayer leader) {
        this.name = name;
        this.addMember(leader, true);
        this.setLeader(leader);
        this.creationDate = new Date();
    }

    /**
     * Constructor that creates an new <code>Clan</code> instance
     * from a valid {@link ClanConfiguration}
     * @see ClanConfiguration#validate(FileConfiguration) 
     * @param configuration a valid ClanConfiguration
     * @throws InvalidClanConfigurationException thrown if {@link ClanConfiguration#validate(FileConfiguration)} returns false
     */
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

        assert configuration.getLeader() != null; // just to get rid of the stupid warning

        this.setLeader(configuration.getLeader());

    }

    /**
     * Add a new member to the clan
     * @param player player who will be add to the clan
     * @param silent if true no message will be displayed when the player is added to the clan
     */
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

    /**
     * Removes a player from clan,
     * does nothing if the player isn't a member of the clan
     * @param player the player who will get removed
     * @param reason the reason why they left the clan
     */
    public void removeMember(@NotNull OfflinePlayer player, @NotNull Clan.LeaveReason reason) {
        PlayerLeaveClanEvent event = new PlayerLeaveClanEvent(player, this, reason);
        if (!event.isCancelled() && this.members.containsKey(player)) {
            this.members.get(player).leaveClan(false);
        }

        this.members.remove(player);
    }

    /**
     * Sets a new leader to the clan,
     * the new leader is a player who must be a member in the same clan
     * and the old leader will get demoted to a lower rank
     * @param leader new leader
     */
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

    /**
     * Gets the clan's name
     * @return name of the clan
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the clan's description
     * @return description of the clan
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the creation date of the clan
     * @return Clan's creation <code>Date</code>
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Gets the leader of the clan
     * @return Leader's <code>ClanMember</code> instance
     */
    public ClanMember getLeader() {
        return this.leader;
    }

    /**
     * Gets the total kills of the clan,
     * the value isn't affected when a player leaves the clan
     * as it is independent from total members' kills
     * @return clan's total kills
     */
    public int getKills() {
        return this.totalKills;
    }

    /**
     * Gets the clan's level
     * @return level of the clan
     */
    public long getLevel() {
        return this.level;
    }

    /**
     * Gets the ClanMember instance of a given player
     * this method returns null if the player isn't an actual member of the clan
     * @param player the player whose associated to a <code>ClanMember</code>
     * @return a <code>ClanMember</code> instance, null if the player wasn't found in the clan
     */
    @Nullable
    public ClanMember getMember(OfflinePlayer player) {
        return this.members.get(player);
    }

    /**
     * Checks whether a player is a member of the clan
     * @param player the player which will be examined
     * @return true if the player is a found within clan's members
     */
    public boolean hasMember(OfflinePlayer player) {
        return this.members.containsKey(player);
    }

    /**
     * Gets all members of the clan
     * @return an <code>ClanMember</code> Array
     */
    public ClanMember[] getMembers() {
        return this.members.values().toArray(new ClanMember[0]);
    }

    /**
     * Gets the current clan's state
     * @return Status value
     */
    public Clan.Status getStatus() {
        return this.state;
    }

    /**
     * Sets the clan's state
     * @param newState newState - represent the new state for the clan
     */
    public void setStatus(@NotNull Clan.Status newState) {
        this.state = newState;
    }

    /**
     * Sets description of the clan
     * @param description new description
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    /**
     * Sets clan's kills
     * @param kills total kills
     */
    public void setKills(int kills) {
        this.totalKills = Math.max(kills, 0);
    }

    /**
     * Sets clan's level
     * @param level level value
     */
    public void setLevel(long level) {
        this.level = Math.max(level, 0L);
    }

    /**
     * Send a message to all clan members
     * @param message message to send
     */
    public void sendMessage(String message) {
        for (ClanMember member : this.members.values()) {
            member.sendMessage(message);
        }
    }

    /**
     * This method is similar to {@link #sendMessage(String)}
     * but it takes an extra parameter `<b>broadcaster</b>`
     * it sends a message to all clan members with including the one who sent the broadcast
     * @param message message to send
     * @param broadcaster the sender
     */
    public void broadcast(String message, Player broadcaster) {
        sendMessage("[" + this.name +  "] " + broadcaster.getDisplayName() + " >> " + message);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Clan && ((Clan) object).getName().equals(this.name);
    }

    /**
     * Save the clan to disk at {@link ClanConfiguration#defaultPath}
     * @throws IOException thrown an I/O exception occur while saving the configuration file
     */
    public void save() throws IOException {
        ClanConfiguration configuration = ClanConfiguration.fromClan(this);
        configuration.save();
    }

    /**
     * delete the clan, this will kick all the players and removes the clan from cache
     * also deletes its configuration file from disk at the default save path
     */
    public void delete() {
        ClanDeleteEvent event = new ClanDeleteEvent(this);
        plugin.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {

            for (ClanMember member : members.values()) {
                this.removeMember(member.getPlayer(), Clan.LeaveReason.KICK);
            }

            String childPath = File.separator + this.name.toLowerCase() + ".yml";
            File file = new File(ClanConfiguration.defaultPath, childPath);
            final boolean success = file.delete();
            if (!success) ClansPlugin.log("Could not deleted file at path " + file.getPath());
            plugin.removeClanCache(this.name);
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
