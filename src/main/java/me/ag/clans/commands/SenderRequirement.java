package me.ag.clans.commands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.Messages.Errors;
import me.ag.clans.messages.Messages.MessageKey;
import me.ag.clans.messages.formatter.Formatter;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SenderRequirement {
    NONE(sender -> true, null),
    PLAYER_ONLY(sender -> sender instanceof Player, Errors.PLAYER_ONLY_COMMAND),
    WITH_CLAN(sender -> sender instanceof Player && getPlugin().getPlayerClan((Player) sender) != null, Errors.NO_CLAN),
    WITHOUT_CLAN(sender -> sender instanceof Player && getPlugin().getPlayerClan((Player) sender) == null, Errors.ALREADY_HAVE_CLAN),
    CONSOLE(sender -> sender instanceof ConsoleCommandSender, null);

    private final SenderCheck match;
    private final MessageKey errorMessage;

    SenderRequirement(@NotNull SenderCheck match, @Nullable MessageKey errorMessage) {
        this.match = match;
        this.errorMessage = errorMessage;
    }

    public boolean match(@NotNull CommandSender sender) {
        return this.match.check(sender);
    }

    @Nullable
    public MessageKey getErrorMessage() {
        return this.errorMessage;
    }

    public void sendErrorMessage(CommandSender sender, Formatter... formatters) {
        if (getErrorMessage() != null) {
            Messages messages = ClansPlugin.getInstance().getMessages();
            sender.sendMessage(messages.getMessage(getErrorMessage(), formatters));
        }
    }

    private static ClansPlugin getPlugin() {
        ClansPlugin plugin = ClansPlugin.getInstance();
        if (plugin == null)
            throw new RuntimeException("ClanZ plugin isn't enabled");
        return plugin;
    }
}
