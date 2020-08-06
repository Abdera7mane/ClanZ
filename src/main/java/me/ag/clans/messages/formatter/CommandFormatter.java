package me.ag.clans.messages.formatter;

import me.ag.clans.commands.subcommands.SubCommand;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;

public class CommandFormatter implements Formatter {
    private final SubCommand command;

    public CommandFormatter(SubCommand command) {
        this.command = command;
    }

    @NotNull
    @Override
    public String format(@NotNull String message) {
        String formatted = message;

        final String label = this.command.getLabel();
        final String description = this.command.getDescription();
        final String usage = this.command.getUsage();

        formatted = StringUtils.replace(formatted, "{command.label}", label);
        formatted = StringUtils.replace(formatted, "{command.description}", description);
        formatted = StringUtils.replace(formatted, "{command.usage}", usage);

        return formatted;
    }
}
