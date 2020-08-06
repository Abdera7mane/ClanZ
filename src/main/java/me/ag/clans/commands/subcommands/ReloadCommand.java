package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
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
        ClansPlugin plugin = ClansPlugin.getInstance();
        boolean success = plugin.loadConfig();
        if (success) {
            sender.sendMessage("§2configuration reloaded !");
        }
        else {
            sender.sendMessage("§4Couldn't reload plugin's configuration, please check the logs");
        }
        return true;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public boolean clanRequired() {
        return false;
    }
}
