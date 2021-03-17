package me.ag.clans.messages.formatter;

import me.ag.clans.commands.subcommands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandFormatter implements Formatter {

    private final SubCommand sender;

    public CommandFormatter(@NotNull SubCommand console) {
        this.sender = console;
    }

    @Override
    public @NotNull String format(@NotNull String message) {
        return message;
    }
}