package me.ag.clans.configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.ag.clans.types.ClanMember;
import me.ag.clans.types.ClanRole;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ClanMemberConfigurationSection extends YamlConfiguration {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final OfflinePlayer player;

    public ClanMemberConfigurationSection(@NotNull OfflinePlayer player) {
        this.player = player;
    }

    public static ClanMemberConfigurationSection fromClanMember(@NotNull ClanMember member) {
        ClanMemberConfigurationSection configuration = new ClanMemberConfigurationSection(member.getPlayer());
        configuration.set("join-date", dateFormat.format(member.getJoinDate()));
        configuration.set("role", member.getRole().toString());
        configuration.set("kills", member.getKills());
        return configuration;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public Date getJoinDate() {
        Date date = new Date();
        if (this.isString("join-date")) {
            try {
                date = dateFormat.parse(this.getString("join-date"));
            } catch (ParseException var3) {
                date = new Date();
            }
        }

        return date;
    }

    public ClanRole getRole() {
        return ClanRole.valueOf(this.getString("role", "MEMBER"));
    }

    public int getKills() {
        return this.getInt("kills", 0);
    }
}
