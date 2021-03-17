package me.abdera7mane.clans.messages;

import me.abdera7mane.clans.messages.formatter.CommandFormatter;
import me.abdera7mane.clans.messages.formatter.Formatter;
import me.abdera7mane.clans.commands.subcommands.SubCommand;

import me.abdera7mane.clans.types.ClanRole;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class Messages {
    private final FileConfiguration configuration;

    public interface MessageKey {
        @NotNull String getKeyPath();
    }

    public enum Globals implements MessageKey {
        CLAN_CREATED("clan-created"),
        CLAN_DELETED("clan-deleted"),
        CLAN_CLOSED("clan-closed"),
        JOIN_CLAN("you-have-joined-the-clan"),
        PLAYER_JOINED("on-clan-join"),
        LEAVE_CLAN("you-have-left-the-clan"),
        PLAYER_LEFT("on-clan-leave"),
        INVITATION_SENT("invitation-sent"),
        INVITATION_ACCEPTED("invitation-accepted"),
        INVITATION_DENIED("invitation-denied"),
        INVITATION_EXPIRED("invitation-expired"),
        MEMBER_PROMOTED("member-promoted"),
        MEMBER_DEMOTED("member-demoted"),
        NEW_LEADER("new-leader");

        private final String keyPath;


        Globals(String keyPath) {
            this.keyPath = "global." + keyPath;
        }

        @NotNull
        @Override
        public String getKeyPath() {
            return this.keyPath;
        }
    }

    public enum Errors implements MessageKey {
        NO_PERMISSION("no-permission"),
        NO_CLAN_PERMISSION("no-clan-permission"),
        INVALID_CLAN_NAME("invalid-clan-name"),
        NO_SPACES_IN_CLAN_NAME("no-spaces-in-clan-name"),
        CLAN_EXISTS("clan-exists"),
        CLAN_NOT_FOUND("clan-not-found"),
        ALREADY_HAVE_CLAN("already-have-clan"),
        PLAYER_ONLY_COMMAND("player-only-command"),
        NO_CLAN("no-clan");

        private final String keyPath;

        Errors(String keyPath) {
            this.keyPath = "errors." + keyPath;
        }

        @NotNull
        @Override
        public  String getKeyPath() {
            return this.keyPath;
        }
    }

    public Messages(FileConfiguration currentConfig) {
        this.configuration = currentConfig;
    }


    public FileConfiguration getConfig() {
        return this.configuration;
    }


    @SuppressWarnings("ConstantConditions")
    @NotNull
    private String getColored(@NotNull String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, ""));
    }

    @NotNull
    private String getFormatted(@NotNull String path, boolean withPrefix, @NotNull Formatter... formatters) {
        final String prefix = getPrefix();
        final boolean prefixed = getConfig().getBoolean("prefixed", true);

        String message = getColored(path);
        for (Formatter formatter : formatters) {
            message = formatter.format(message);
        }

        return (prefixed && withPrefix ? prefix + " " : "") + message;
    }

    @NotNull
    public String getPrefix() {
        return this.getColored("prefix");
    }

    @NotNull
    public String getMessage(@NotNull MessageKey message, @NotNull Formatter... formatters) {
        return getMessage(message, true, formatters);
    }

    @NotNull
    public String getMessage(@NotNull MessageKey message, boolean withPrefix, @NotNull Formatter... formatters) {
        return getFormatted(message.getKeyPath(), withPrefix, formatters);
    }
    
    public String getRoleDisplayName(@NotNull ClanRole role) {
        return this.getColored("labels.roles." + role.toString().toLowerCase());
    }
    
    @NotNull
    public String getSubCommandDescription(@NotNull SubCommand command) {
        return getColored("commands." + command.getLabel() + ".description");
    }

    @NotNull
    public String getSubCommandUsage(@NotNull SubCommand command) {
        return getColored("commands." + command.getLabel() + ".usage");
    }

    @NotNull
    public String getHelpFormat(SubCommand command) {
        return getFormatted("help-format", true, new CommandFormatter(command));
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull CommandSender to, Formatter... formatters) {
        this.sendMessage(messageKey, to, true, formatters);
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull CommandSender to, boolean withPrefix, Formatter... formatters) {
        final String message = this.getFormatted(messageKey.getKeyPath(), withPrefix, formatters);
        final Sound sound = null;
        to.sendMessage(message);
        if (to instanceof Player) {
            Player player = (Player) to;
            final Location location = player.getLocation();
            if (sound != null)
                player.playSound(location, sound, 1.0f, 1.0f);
        }
    }
}
