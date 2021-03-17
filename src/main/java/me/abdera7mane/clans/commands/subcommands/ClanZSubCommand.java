package me.abdera7mane.clans.commands.subcommands;

import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.ClansPlugin;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ClanZSubCommand extends SubCommand {
    private final ClansPlugin plugin;

    public ClanZSubCommand(@NotNull String label, @NotNull ClansPlugin owner) {
        this(label, owner, new ArrayList<>());
    }

    public ClanZSubCommand(@NotNull String label, @NotNull ClansPlugin owner, @NotNull List<String> aliases) {
        super(label, owner, aliases);
        this.plugin = owner;
    }

    public ClansPlugin getPlugin() {
        return this.plugin;
    }

    public Messages getMessages() {
        return this.getPlugin().getMessages();
    }
}
