package me.ag.clans.commands;

import me.ag.clans.commands.subcommands.BroadcastCommand;
import me.ag.clans.commands.subcommands.ChatCommand;
import me.ag.clans.commands.subcommands.CreateCommand;
import me.ag.clans.commands.subcommands.DeleteCommand;
import me.ag.clans.commands.subcommands.DemoteCommand;
import me.ag.clans.commands.subcommands.JoinCommand;
import me.ag.clans.commands.subcommands.KickCommand;
import me.ag.clans.commands.subcommands.LeaveCommand;
import me.ag.clans.commands.subcommands.PromoteCommand;
import me.ag.clans.commands.subcommands.ReloadCommand;
import me.ag.clans.commands.subcommands.SubCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanCommand extends Command implements TabCompleter {
    private final Map<String, SubCommand> subCommandMap = new LinkedHashMap<>();


    public ClanCommand() {
        super("clan");
        setDescription("The main Clans command");
        List<String> aliases = new ArrayList<>();
        aliases.add("clans");
        setAliases(aliases);

        registerSubCommand(new BroadcastCommand());
        registerSubCommand(new ChatCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new DeleteCommand());
        registerSubCommand(new DemoteCommand());
        registerSubCommand(new JoinCommand());
        registerSubCommand(new KickCommand());
        registerSubCommand(new LeaveCommand());
        registerSubCommand(new PromoteCommand());
        registerSubCommand(new ReloadCommand());
    }

    public boolean registerSubCommand(@NotNull SubCommand command) {
        String label = command.getLabel().toLowerCase();
        if (!subCommandMap.containsKey(label)) {
            subCommandMap.put(label, command);
            registerSubCommand(command.getAliases(), command);
            return true;
        }
        return false;
    }

    public void registerSubCommand(@NotNull List<String> aliases, @NotNull SubCommand command) {
        for (String alias : aliases) {
            alias = alias.toLowerCase();
            if (!subCommandMap.containsKey(alias)) {
                subCommandMap.put(alias, command);
            }
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        String arg0 = args[0].toLowerCase();
        if (subCommandMap.containsKey(arg0)) {
            SubCommand subCommand = subCommandMap.get(arg0);
            if (subCommand.isPlayerCommand() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§cthis is a player only command");
                return true;
            }
            boolean success = subCommand.execute(sender, commandLabel, args);
            if (!success) sender.sendMessage(subCommand.getUsage());
        } else {
            sender.sendMessage("§c" + arg0 + " Command not found, please type:§r /clan help");
        }
        return true;
    }
    private void sendHelp(CommandSender sender) {
        for (SubCommand subCommand : new HashSet<>((subCommandMap.values()))) {
            sender.sendMessage("[§4§lClans§r] " + subCommand.getLabel() + " | " + subCommand.getDescription());

        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.equals(this) && args.length <= 1) {
            List<String> list = new ArrayList<>();
            for (String argument : subCommandMap.keySet()) {
                String arg0 = args.length > 0 ? args[0] : "";
                if (argument.startsWith(arg0)) {
                    list.add(argument);
                }
            }
            return list;
        }

        return null;
    }
}