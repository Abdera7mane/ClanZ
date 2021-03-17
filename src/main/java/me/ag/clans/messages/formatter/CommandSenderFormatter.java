package me.ag.clans.messages.formatter;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandSenderFormatter implements Formatter {

    private final CommandSender sender;

    public CommandSenderFormatter(@NotNull CommandSender console) {
        this.sender = console;
    }

    @Override
    public @NotNull String format(@NotNull String message) {
        return message;
    }
}