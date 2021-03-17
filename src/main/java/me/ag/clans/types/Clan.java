package me.ag.clans.types;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import me.ag.clans.events.ClanDeleteEvent;
import me.ag.clans.events.PlayerJoinClanEvent;
import me.ag.clans.events.PlayerLeaveClanEvent;
import me.ag.clans.messages.formatter.Formatter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Clan implements Metadatable, Formatter {
    private final String name;
    private final Date creationDate;
    private final Map<UUID, ClanMember> members = new HashMap<>();
    private final Map<String, List<MetadataValue>> metadataValues = new HashMap<>();
    private ClanMember leader;
    private String description = "";
    private int level;
    private int totalKills;
    private Status state = Status.PUBLIC;

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
        /**
         * The player left on his own desire
         */
        QUIT,
        /**
         * Kicked by a clan's member
         */
        KICK,
        /**
         * Kicked because the clan was deleted
         */
        DELETE

    }

    /**
     * Constructor which creates a new <code>Clan</code> instance
     * from a defined <i>name</i> and a <i>leader</i> and takes default clan configuration
     *
     * @param name   name of the clan
     * @param leader owner of the clan
     */
    public Clan(@NotNull String name, @NotNull OfflinePlayer leader) {
        this.name = name;
        this.creationDate = new Date();
        ClanMember member = new ClanMember(this, leader);
        member.setRole(ClanRole.LEADER);
        this.members.put(leader.getUniqueId(), member);
        this.leader = member;
    }

    /**
     * Constructor that creates an new <code>Clan</code> instance from a {@link ClanBuilder}
     *
     * @param builder {@link ClanBuilder} instance
     */
    public Clan(@NotNull ClanBuilder builder) {

        this.name = builder.getName();
        this.creationDate = builder.getCreationDate();
        this.setDescription(builder.getDescription());
        this.setKills(builder.getKills());
        this.setLevel(builder.getLevel());
        this.setStatus(builder.getStatus());

        for (ClanMemberBuilder memberBuilder : builder.getMemberBuilders()) {
            UUID uuid = memberBuilder.getOfflinePlayer().getUniqueId();
            this.members.put(uuid, memberBuilder.build(this));
        }

        this.leader = this.getMember(builder.getLeader());

    }

    /**
     * Add a new member to the clan
     *
     * @param player player who will be add to the clan
     */
    public void addMember(@NotNull OfflinePlayer player) {
        if (!this.hasMember(player)) {
            PlayerJoinClanEvent event = new PlayerJoinClanEvent(player, this);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            ClanMember member = new ClanMember(this, player);
            this.members.put(player.getUniqueId(), member);
        }
    }

    /**
     * Removes a player from clan,
     * does nothing if the player isn't a member of the clan
     *
     * @param player the player who will get removed
     * @param reason the reason why they left the clan
     */

    @SuppressWarnings("ConstantConditions")
    public void removeMember(@NotNull OfflinePlayer player, @NotNull Clan.LeaveReason reason) {
        if (!this.hasMember(player)) {
            return;
        }
        PlayerLeaveClanEvent event = new PlayerLeaveClanEvent(player, this, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ClanMember member = this.getMember(player.getUniqueId());
            member.leaveClan();
            this.members.remove(player.getUniqueId());
        }


    }

    /**
     * Sets a new leader to the clan,
     * the new leader is a player who must be a member in the same clan
     * and the old leader will get demoted to a lower rank
     *
     * @param newLeader new leader
     */
    @SuppressWarnings("ConstantConditions")
    public void setLeader(@NotNull OfflinePlayer newLeader) {
        if (newLeader != null && this.hasMember(newLeader)) {
            if (this.leader != null) {
                if (this.leader.getOfflinePlayer() == newLeader) {
                    return;
                }

                this.leader.demote();
            }

            ClanMember member = this.getMember(newLeader);
            member.setRole(ClanRole.LEADER);
            this.leader = member;
        }

    }

    /**
     * Gets the clan's name
     *
     * @return name of the clan
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the clan's description
     *
     * @return description of the clan
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the creation date of the clan
     *
     * @return Clan's creation <code>Date</code>
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Gets the leader of the clan
     *
     * @return Leader's <code>ClanMember</code> instance
     */
    public ClanMember getLeader() {
        return this.leader;
    }

    /**
     * Gets the total kills of the clan,
     * the value isn't affected when a player leaves the clan
     * as it is independent from total members' kills
     *
     * @return clan's total kills
     */
    public int getKills() {
        return this.totalKills;
    }

    /**
     * Gets the clan's level
     *
     * @return level of the clan
     */
    public long getLevel() {
        return this.level;
    }

    /**
     * Gets the ClanMember instance of a given player
     * this method returns null if the player isn't an actual member of the clan
     *
     * @param player the player whose associated to a <code>ClanMember</code>
     * @return a <code>ClanMember</code> instance, null if the player wasn't found in the clan
     */
    @Nullable
    public ClanMember getMember(@NotNull OfflinePlayer player) {
        return this.getMember(player.getUniqueId());
    }

    @Nullable
    public ClanMember getMember(UUID playerUUID) {
        return this.members.get(playerUUID);
    }

    /**
     * Checks whether a player is a member of the clan
     *
     * @param player the player which will be examined
     * @return true if the player is a found within clan's members
     */
    public boolean hasMember(OfflinePlayer player) {
        return this.hasMember(player.getUniqueId());
    }

    public boolean hasMember(UUID playerUUID) {
        return this.members.containsKey(playerUUID);
    }

    public Collection<ClanMember> getMembers() {
        return this.getMembers(false);
    }

    /**
     * Gets all members of the clan
     *
     * @param online weather it should return online members only or otherwise
     * @return a <code>Collection of</code> <code>ClanMember</code>s
     */
    public Collection<ClanMember> getMembers(boolean online) {
        return this.members.values().stream()
                   .filter((member) -> !online || member.isOnline())
                   .collect(Collectors.toSet());
    }

    /**
     * Gets the current clan's state
     *
     * @return Status value
     */
    public Status getStatus() {
        return this.state;
    }

    /**
     * Sets the clan's state
     *
     * @param newState newState - represent the new state for the clan
     */
    public void setStatus(@NotNull Clan.Status newState) {
        this.state = newState;
    }

    /**
     * Sets description of the clan
     *
     * @param description new description
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    /**
     * Sets clan's kills
     *
     * @param kills total kills
     */
    public void setKills(int kills) {
        this.totalKills = Math.max(kills, 0);
    }

    /**
     * Sets clan's level
     *
     * @param level level value
     */
    public void setLevel(int level) {
        this.level = Math.max(level, 0);
    }

    /**
     * Send a message to all clan members
     *
     * @param message message to send
     */
    public void sendMessage(String message) {
        this.getMembers(true).forEach(member -> member.sendMessage(message));
    }

    /**
     * This method is similar to {@link #sendMessage(String)}
     * but it takes an extra parameter `<b>broadcaster</b>`
     * it sends a message to all clan members including the one who sent the broadcast
     *
     * @param message     message to send
     * @param broadcaster the sender
     */
    public void broadcast(String message, @NotNull Player broadcaster) {
        this.sendMessage("[" + this.name + "] " + broadcaster.getDisplayName() + " >> " + message);
    }

    /**
     * delete the clan, this will kick all the players and removes the clan from cache
     * also deletes its configuration file from disk at the default save path
     */
    public void delete() {
        ClanDeleteEvent event = new ClanDeleteEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.getMembers().forEach(member -> this.removeMember(member.getOfflinePlayer(), LeaveReason.DELETE));
        }
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer(@NotNull String name) {
        for (ClanMember member : this.getMembers()) {
            OfflinePlayer target = member.getOfflinePlayer();
            String targetName = target.getName();

            if (targetName == null) continue;

            if (targetName.equals(name)) {
                return target;
            }
        }

        return null;
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        List<MetadataValue> values = this.getMetadata(metadataKey);
        this.clearMetaData(metadataKey, newMetadataValue.getOwningPlugin());
        values.add(newMetadataValue);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        this.metadataValues.putIfAbsent(metadataKey, new ArrayList<>());
        return this.metadataValues.get(metadataKey);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        return this.metadataValues.containsKey(metadataKey);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        this.clearMetaData(metadataKey, owningPlugin);
    }

    private void clearMetaData(String metadataKey, Plugin owningPlugin) {
        this.getMetadata(metadataKey)
            .removeIf(value -> Objects.equals(value.getOwningPlugin(), owningPlugin));
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    @Override
    public String format(@NotNull String message) {
        String formatted = message;

        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        final String formattedDate = dateFormat.format(this.getCreationDate());

        formatted = formatted.replace("{clan}", this.getName());
        formatted = formatted.replace("{clan.leader}", this.getLeader().getOfflinePlayer().getName());
        formatted = formatted.replace("{clan.description}", this.getDescription());
        formatted = formatted.replace("{clan.kills}", String.valueOf(this.getKills()));
        formatted = formatted.replace("{clan.level}", String.valueOf(this.getLevel()));
        formatted = formatted.replace("{clan.status}", this.getStatus().toString());
        formatted = formatted.replace("{clan.members}", String.valueOf(this.members.size()));
        formatted = formatted.replace("{clan.creationdate}", formattedDate);

        return formatted;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Clan && ((Clan) object).getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        int hash = name.hashCode();
        hash = 31 * hash + creationDate.hashCode();
        return hash;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                  .append("[name=")
                  .append(this.getName())
                  .append(", leader=")
                  .append(this.getLeader().getOfflinePlayer().getUniqueId())
                  .append(", members.size=")
                  .append(this.members.size())
                  .append("]")
                  .toString();
    }
}