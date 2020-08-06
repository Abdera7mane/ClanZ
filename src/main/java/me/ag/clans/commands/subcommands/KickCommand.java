package me.ag.clans.commands.subcommands;

import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class KickCommand extends SubCommand {
    public KickCommand() {
        super("kick");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    public boolean clanRequired() {
        return true;
    }
}
