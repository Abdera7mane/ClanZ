package me.abdera7mane.clans.messages.formatter;

import me.abdera7mane.clans.commands.subcommands.SubCommand;
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