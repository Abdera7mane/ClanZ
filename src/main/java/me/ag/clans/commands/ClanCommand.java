package me.ag.clans.commands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.commands.subcommands.BroadcastCommand;
import me.ag.clans.commands.subcommands.ChatCommand;
import me.ag.clans.commands.subcommands.CreateCommand;
import me.ag.clans.commands.subcommands.DeleteCommand;
import me.ag.clans.commands.subcommands.DemoteCommand;
import me.ag.clans.commands.subcommands.HelpCommand;
import me.ag.clans.commands.subcommands.JoinCommand;
import me.ag.clans.commands.subcommands.KickCommand;
import me.ag.clans.commands.subcommands.LeaveCommand;
import me.ag.clans.commands.subcommands.PromoteCommand;
import me.ag.clans.commands.subcommands.ReloadCommand;
import me.ag.clans.commands.subcommands.SubCommand;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.formatter.CommandFormatter;
import me.ag.clans.messages.formatter.PlayerFormatter;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanCommand extends Command implements TabCompleter {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();
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
        registerSubCommand(new HelpCommand());
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

    @Nullable
    public SubCommand getCommand(@NotNull String name) {
        return this.subCommandMap.get(name);
    }

    public Map<String, SubCommand> getAllCommands() {
        return this.subCommandMap;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        Messages messages = plugin.getMessages();
        if (args.length == 0) {
            this.subCommandMap.get("help").execute(sender, commandLabel, args);
            return true;
        }

        String arg0 = args[0].toLowerCase();
        if (subCommandMap.containsKey(arg0)) {
            SubCommand subCommand = subCommandMap.get(arg0);
            if (subCommand.isPlayerCommand() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§cthis is a player only command");
                return true;
            }
            else if (subCommand.clanRequired() && sender instanceof Player) {
                Player player = (Player) sender;
                if (!PlayerUtilities.hasClan(player)) {
                    String message = messages.getErrorMessage(
                            Messages.Errors.PLAYER_ONLY_COMMAND,
                            new PlayerFormatter(player),
                            new CommandFormatter(subCommand));
                    player.sendMessage(message);
                    return true;
                }
            }

            boolean success = subCommand.execute(sender, commandLabel, args);
            if (!success) sender.sendMessage(subCommand.getUsage());
        } else {
            sender.sendMessage("§c" + arg0 + " Command not found, please type:§r /clan help");
        }
        return true;
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