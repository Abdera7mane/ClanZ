package me.ag.clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PluginCommandBuilder {
    private final String label;
    private final Plugin owner;
    private String description = "";
    private List<String> aliases = new ArrayList<>();
    private String permission;
    private String permissionMessage;
    private String usage = "";
    private CommandExecutor commandExecutor;
    private TabCompleter tabCompleter;

    public PluginCommandBuilder(@NotNull String label, @NotNull Plugin owner) {
        this.label = label;
        this.owner = owner;
    }

    public PluginCommandBuilder description(@NotNull String description) {
        this.description = description;
        return this;
    }

    public PluginCommandBuilder setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public PluginCommandBuilder withPermission(@Nullable String permission) {
        this.permission = permission;
        return this;
    }

    public PluginCommandBuilder permissionMessage(@Nullable String permissionMessage) {
        this.permissionMessage = permissionMessage;
        return this;
    }

    public PluginCommandBuilder usage(@NotNull String usage) {
        this.usage = usage;
        return this;
    }

    public PluginCommandBuilder commandExecutor(@Nullable CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public PluginCommandBuilder tabCompleter(@Nullable TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
        return this;
    }

    @Nullable
    public PluginCommand build() {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            command = constructor.newInstance(label, owner);
            command.setDescription(description)
                    .setAliases(aliases)
                    .setUsage(usage);
            command.setPermission(permission);
            command.setPermissionMessage(permissionMessage);
            command.setExecutor(commandExecutor);
            command.setTabCompleter(tabCompleter);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return command;
    }

    public boolean register() {
        PluginCommand command = this.build();

        boolean result = false;

        CommandMap commandMap = getCommandMap();

        if (commandMap != null && command != null) {
            result = commandMap.register(command.getLabel(), command);
        }

        return result;
    }

    @Nullable
    private CommandMap getCommandMap() {
        final Server server = Bukkit.getServer();
        
        CommandMap commandMap = null;
        try {
            Field bukkitCommandMap = server.getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(server);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }
}
