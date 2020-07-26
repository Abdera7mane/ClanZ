package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public abstract class SubCommand {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
    private final String label;
    private final List<String> aliases;

    public SubCommand(String label) {
        this.label = label.toLowerCase();
        this.aliases = new ArrayList<>();
    }

    public SubCommand(String label, List<String> aliases) {
        this.label = label.toLowerCase();
        this.aliases = aliases;
    }

    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args);

    public abstract boolean isPlayerCommand();

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return plugin.getMessages().getSubCommandDescription(this);
    }

    public String getUsage() {
        return plugin.getMessages().getSubCommandUsage(this);
    }

    public List<String> getAliases() {
        return aliases;
    }
}
