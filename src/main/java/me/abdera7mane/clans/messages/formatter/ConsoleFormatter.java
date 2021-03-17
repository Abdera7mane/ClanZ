package me.abdera7mane.clans.messages.formatter;

import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleFormatter implements Formatter {

    private final ConsoleCommandSender console;

    ConsoleFormatter(@NotNull ConsoleCommandSender console) {
        this.console = console;
    }

    @Override
    public @NotNull String format(@NotNull String message) {
        String formatted = message;

        String name = console.getName();

        formatted = formatted.replace("{player}", name);
        formatted = formatted.replace("{displayname}", name);
        formatted = formatted.replace("{sender}", name);

        return formatted;
    }
}
