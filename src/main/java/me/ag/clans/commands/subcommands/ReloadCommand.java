package me.ag.clans.commands.subcommands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends SubCommand{
    public ReloadCommand() {
        super("reload", new ArrayList<>(Arrays.asList("rl")));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }
}
