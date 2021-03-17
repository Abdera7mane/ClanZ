package me.ag.clans.types;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClanBuilder {
    private final String name;
    private final OfflinePlayer leader;
    private String description;
    private Date creationDate;
    private int kills;
    private int level;
    private Clan.Status status;
    private final Set<ClanMemberBuilder> memberBuilders = new HashSet<>();

    public ClanBuilder(@NotNull String name, @NotNull UUID leaderUUID) {
        this(name, Bukkit.getOfflinePlayer(leaderUUID));
    }

    public ClanBuilder(@NotNull String name, @NotNull OfflinePlayer leader) {
        this.name = name;
        this.leader = leader;
        this.setDescription("")
            .setCreationDate(new Date());
    }

    public ClanBuilder setDescription(String description) {
        this.description = description != null ? description : "";
        return this;
    }

    public ClanBuilder setCreationDate(Date date) {
        this.creationDate = date != null ? date : new Date();
        return this;
    }

    public ClanBuilder  setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public ClanBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public ClanBuilder addMemberBuilder(ClanMemberBuilder memberBuilder) {
        this.memberBuilders.add(memberBuilder);
        return this;
    }

    public ClanBuilder addAllMemberBuilders(Collection<? extends ClanMemberBuilder> memberBuilders) {
        this.memberBuilders.addAll(memberBuilders);
        return this;
    }

    public Clan build() {
        return new Clan(this);
    }

    public String getName() {
        return this.name;
    }

    public OfflinePlayer getLeader() {
        return this.leader;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public int getKills() {
        return this.kills;
    }

    public String getDescription() {
        return this.description;
    }

    public int getLevel() {
        return this.level;
    }

    public Clan.Status getStatus() {
        return this.status;
    }

    public Set<ClanMemberBuilder> getMemberBuilders() {
        return this.memberBuilders;
    }
}
