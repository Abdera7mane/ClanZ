package me.ag.clans.messages;

import me.ag.clans.ClansPlugin;
import me.ag.clans.commands.subcommands.SubCommand;

import me.ag.clans.messages.formatter.Formatter;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class Messages {
    private final FileConfiguration configuration = new YamlConfiguration();
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public Messages(Reader reader) {
        File messagesConfigFile = new File(plugin.getDataFolder(), File.separator + "messages.yml");
        if (!messagesConfigFile.isFile()) {
            plugin.saveResource("messages.yml", false);
        }

        FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        try {
            configuration.load(messagesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        messagesConfig.addDefaults(configuration);

        try {
            messagesConfig.save(messagesConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public FileConfiguration getConfig() {
        return this.configuration;
    }

    @NotNull
    public String getMessage(@NotNull String path, Formatter @NotNull ... formatters) {
        final String prefix = this.configuration.getString("prefix", "");
        final boolean prefixed = this.configuration.getBoolean("prefixed", true);

        String message = this.configuration.getString(path, "");
        message = ChatColor.translateAlternateColorCodes('&', message);
        for (Formatter formatter : formatters) {
            message = formatter.format(message);
        }
        return prefixed ? prefix : "" + message;
    }

    public String getErrorMessage(String err, Formatter... formatters) {
        return getMessage("errors."+ err, formatters);
    }

    public String getSubCommandDescription(@NotNull SubCommand command, Formatter... formatters) {
        return getMessage("commands." + command.getLabel() + ".description", formatters);
    }

    public String getSubCommandUsage(@NotNull SubCommand command, Formatter... formatters) {
        return getMessage("commands." + command.getLabel() + ".usage", formatters);
    }

}
