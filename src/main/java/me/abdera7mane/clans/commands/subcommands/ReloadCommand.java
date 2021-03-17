package me.abdera7mane.clans.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.util.LoadResult;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReloadCommand extends ClanZSubCommand{

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public ReloadCommand(ClansPlugin owner) {
        super("reload", owner, new ArrayList<>(Arrays.asList("rl")));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        LoadResult result = this.getPlugin().loadConfigs();
        if (result.succeeded()) {
            sender.sendMessage("§2configuration reloaded !");
        }
        else {
            sender.sendMessage("§4Couldn't reload plugin's configuration, please check the logs");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
