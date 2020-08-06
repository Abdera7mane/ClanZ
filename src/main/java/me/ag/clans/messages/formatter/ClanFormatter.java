package me.ag.clans.messages.formatter;

import me.ag.clans.types.Clan;

import java.text.SimpleDateFormat;

import org.jetbrains.annotations.NotNull;

public class ClanFormatter implements Formatter {
    private final Clan clan;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ClanFormatter(Clan clan) {
        this.clan = clan;
    }

    @NotNull
    @Override
    public String format(@NotNull String message) {
        String formatted = message;

        final String name = this.clan.getName();
        final String leader = this.clan.getLeader().getPlayer().getName();
        final String description = this.clan.getDescription();
        final String kills = String.valueOf(this.clan.getKills());
        final String level = String.valueOf(this.clan.getLevel());
        final String status = this.clan.getStatus().toString();
        final String memberAmount = String.valueOf(this.clan.getMembers().length);
        final String creationDate = dateFormat.format(this.clan.getCreationDate());

        formatted = formatted.replace("{clan}", name);
        formatted = formatted.replace("{clan.leader}", leader);
        formatted = formatted.replace("{clan.description}", description);
        formatted = formatted.replace("{clan.kills}", kills);
        formatted = formatted.replace("{clan.level}", level);
        formatted = formatted.replace("{clan.status}", status);
        formatted = formatted.replace("{clan.members}", memberAmount);
        formatted = formatted.replace("{clan.creationDate}", creationDate);

        return formatted;
    }
}
