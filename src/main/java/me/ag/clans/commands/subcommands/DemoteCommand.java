package me.ag.clans.commands.subcommands;

import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class DemoteCommand extends SubCommand {
    public DemoteCommand() {
        super("demote");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    @Override
    public boolean clanRequired() {
        return true;
    }
}
