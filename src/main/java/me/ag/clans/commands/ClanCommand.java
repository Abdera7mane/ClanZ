package me.ag.clans.commands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.commands.subcommands.*;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.formatter.CommandFormatter;
import me.ag.clans.messages.formatter.CommandSenderFormatter;

import me.ag.clans.messages.formatter.Formatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClanCommand implements CommandExecutor, TabCompleter, Listener {
    private final ClansPlugin plugin;
    private final Map<String, SubCommand> subCommandsMap = new TreeMap<>();
    private final Map<SubCommand, SenderRequirement[]> execRequirementMap = new HashMap<>();
    private SubCommand helpCommand;

    public ClanCommand(ClansPlugin plugin) {
        this.plugin = plugin;

        this.registerSubCommand(new BroadcastCommand(plugin));
        this.registerSubCommand(new ChatCommand(plugin));
        this.registerSubCommand(new CreateCommand(plugin));
        this.registerSubCommand(new DeleteCommand(plugin));
        this.registerSubCommand(new DemoteCommand(plugin));
        this.registerSubCommand(new HelpCommand(plugin, this));
        this.registerSubCommand(new JoinCommand(plugin));
        this.registerSubCommand(new KickCommand(plugin));
        this.registerSubCommand(new LeaveCommand(plugin));
        this.registerSubCommand(new PromoteCommand(plugin));
        this.registerSubCommand(new ReloadCommand(plugin));

        this.setHelpCommand(this.getSubCommand("help"));

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean registerSubCommand(@NotNull SubCommand command) {
        boolean success = false;
        String label = command.getLabel();
        if (!isSubCommandRegistered(label)) {
            this.subCommandsMap.put(label, command);
            this.execRequirementMap.put(command, getRequirements(command.getClass()));
            for (String alias : command.getAliases()) {
                alias = alias.toLowerCase();
                // Prevent aliases collision
                if (!this.subCommandsMap.containsKey(alias)) {
                    this.subCommandsMap.put(alias, command);
                }
            }
            success = true;
        }
        return success;
    }


    public boolean isSubCommandRegistered(@NotNull String commandName) {
        return this.subCommandsMap.containsKey(commandName);
    }

    public void unregisterSubCommand(@NotNull SubCommand command) {
        this.subCommandsMap.remove(command.getLabel());
        for (String alias: command.getAliases()) {
            // Since not all subcommands aliases could be registed
            // We must remove the alias only if it was mapped to the approperiate subcommand
            this.subCommandsMap.remove(alias, command);
        }
    }

    @Nullable
    public SubCommand getSubCommand(@NotNull String name) {
        return this.subCommandsMap.get(name);
    }

    @NotNull
    public Set<SubCommand> getSubCommands() {
        return new TreeSet<>((this.subCommandsMap.values()));
    }

    @Nullable
    public SubCommand getHelpCommand() {
        return this.helpCommand;
    }

    public void setHelpCommand(@Nullable SubCommand helpCommand) {
        this.helpCommand = helpCommand;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Messages messages = this.plugin.getMessages();
        Formatter senderFormatter = new CommandSenderFormatter(sender);

        if (!this.checkPermission(sender)) {
            messages.sendMessage(Messages.Errors.NO_PERMISSION, sender, senderFormatter);
            return true;
        }

        if (args.length == 0) {
            SubCommand helpCommand = this.getHelpCommand();
            if (helpCommand != null) {
                helpCommand.execute(sender, label, args);
            }
            return true;
        }

        String arg0 = args[0].toLowerCase();
        if (this.isSubCommandRegistered(arg0)) {
            SubCommand subCommand = this.getSubCommand(arg0);
            if (!this.checkPermission(sender, subCommand)) {
                return true;
            }

            boolean success = subCommand.execute(sender, label, args);
            if (!success) {
                sender.sendMessage("§4§lError§4 at argument§c " + args.length);
                sender.sendMessage("§eUsage§r " + subCommand.getUsage());

            }
        } else {
            sender.sendMessage("§c" + arg0 + " Command not found, please type:§r /clan help");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        String arg0 = args.length > 0 ? args[0].toLowerCase() : "";
        if (this.isSubCommandRegistered(arg0) && args.length > 1) {
            return this.subCommandsMap.get(arg0).onTabComplete(sender, command, alias, args);
        }

        return this.subCommandsMap.keySet().stream()
                   .filter(commandName -> commandName.startsWith(arg0))
                   .collect(Collectors.toList());
    }

    public boolean checkPermission(@NotNull CommandSender target) {
        final String permission = this.plugin.getMainCommand().getPermission();
        return permission != null && target.hasPermission(permission);
    }

    public boolean checkPermission(@NotNull CommandSender target, @NotNull SubCommand command) {
        boolean check = true;
        for (SenderRequirement requirement : this.execRequirementMap.get(command)) {
            if (!requirement.match(target)) {
                check = false;
                requirement.sendErrorMessage(
                        target,
                        new CommandSenderFormatter(target),
                        new CommandFormatter(command)
                );
                break;
            }
        }
        return check;
    }

    private SenderRequirement[] getRequirements(@NotNull Class<? extends SubCommand> clazz) {
        SubCommandOptions annotation = clazz.getAnnotation(SubCommandOptions.class);
        return annotation.requirements();
    }
    
    @EventHandler
    private void onPluginDisabled(PluginDisableEvent event) {
        for (SubCommand command : this.getSubCommands()) {
            if (command.getOwner().equals(event.getPlugin()))
                this.unregisterSubCommand(command);
        }
    }
    
}