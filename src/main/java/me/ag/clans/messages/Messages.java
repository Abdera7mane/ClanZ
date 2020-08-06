package me.ag.clans.messages;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import me.ag.clans.ClansPlugin;
import me.ag.clans.commands.subcommands.SubCommand;
import me.ag.clans.messages.formatter.CommandFormatter;
import me.ag.clans.messages.formatter.Formatter;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class Messages {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private final FileConfiguration configuration;


    public enum Global {
        CLAN_CREATED("clan-created"),
        CLAN_DELETED("clan-deleted"),
        CLAN_CLOSED("clan-closed"),
        JOIN_CLAN("join-clan"),
        PLAYER_JOINED("player-joined"),
        LEAVE_CLAN("leave-clan"),
        PLAYER_LEFT("player-left"),
        INVITATION_SENT("invitation-sent"),
        INVITATION_ACCEPTED("invitation-accepted"),
        INVITATION_DENIED("invitation-denied"),
        INVITATION_EXPIRED("invitation-expired"),
        MEMBER_PROMOTED("member-promoted"),
        MEMBER_DEMOTED("member-demoted"),
        NEW_LEADER("new-leader");

        public final String keyPath;

        Global(String keyPath) {
            this.keyPath = "global." + keyPath;
        }
    }

    public enum Errors {
        NO_PERMISSION("no-permission"),
        NO_CLAN_PERMISSION("no-clan-permission"),
        INVALID_CLAN_NAME("invalid-clan-name"),
        CLAN_EXISTS("clan-exists"),
        ALREADY_HAVE_CLAN("already-have-clan"),
        PLAYER_ONLY_COMMAND("player-only-command"),
        NO_CLAN("no-clan");

        public final String keyPath;

        Errors(String keyPath) {
            this.keyPath = "errors." + keyPath;
        }
    }


    public Messages(Reader reader) {
        File messagesConfigFile = new File(plugin.getDataFolder(), File.separator + "messages.yml");
        if (!messagesConfigFile.isFile()) {
            plugin.saveResource("messages.yml", false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(messagesConfigFile);

        this.configuration.addDefaults(YamlConfiguration.loadConfiguration(reader));

        try {
            this.configuration.save(messagesConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public FileConfiguration getConfig() {
        return this.configuration;
    }


    @NotNull
    private String getColored(@NotNull String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, ""));
    }

    @NotNull
    private String getFormatted(@NotNull String path, @NotNull Formatter... formatters) {
        final String prefix = getPrefix();
        final boolean prefixed = getConfig().getBoolean("prefixed", true);

        String message = getColored(path);
        for (Formatter formatter : formatters) {
            message = formatter.format(message);
        }
        System.out.println(message);
        return prefixed ? prefix : "" + message;
    }

    @NotNull
    public String getPrefix() {
        return this.getColored("prefix");
    }

    @NotNull
    public String getMessage(@NotNull Messages.Global message, @NotNull Formatter... formatters) {
        return getFormatted(message.keyPath, formatters);
    }

    @NotNull
    public String getErrorMessage(@NotNull Messages.Errors errorMessage, @NotNull Formatter... formatters) {
        return getFormatted(errorMessage.keyPath, formatters);
    }

    @NotNull
    public String getLabel(@NotNull String path) {
        return getColored("labels." + path);
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
        return getFormatted("help-format", new CommandFormatter(command));
    }
}
