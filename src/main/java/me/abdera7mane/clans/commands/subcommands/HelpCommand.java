package me.abdera7mane.clans.commands.subcommands;

import java.util.Collection;
import java.util.List;

import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.commands.ClanCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HelpCommand extends ClanZSubCommand {
    private final ClanCommand command;

    public HelpCommand(ClansPlugin owner, ClanCommand command) {
        super("help", owner);
        setPermission("clanz.help");
        this.command = command;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {

        Collection<SubCommand> commands = command.getSubCommands();
        for (SubCommand subCommand :  commands) {
            sender.sendMessage(this.getMessages().getHelpFormat(subCommand));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
