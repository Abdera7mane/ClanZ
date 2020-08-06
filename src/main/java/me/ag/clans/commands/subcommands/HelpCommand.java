package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.commands.ClanCommand;
import me.ag.clans.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class HelpCommand extends SubCommand {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public HelpCommand() {
        super("help");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        Messages messages = plugin.getMessages();
        ClanCommand command = plugin.getMainCommand();

        Set<SubCommand> commands = new HashSet<>(command.getAllCommands().values());
        for (SubCommand subCommand :  commands) {
            sender.sendMessage(messages.getHelpFormat(subCommand));
        }

        return true;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public boolean clanRequired() {
        return false;
    }
}
