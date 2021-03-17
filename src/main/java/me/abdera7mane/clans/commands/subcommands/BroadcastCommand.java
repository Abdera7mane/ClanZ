package me.abdera7mane.clans.commands.subcommands;

import java.util.List;

import me.abdera7mane.clans.ClansPlugin;
import static me.abdera7mane.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.abdera7mane.clans.commands.SenderRequirement.WITH_CLAN;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITH_CLAN})
public final class BroadcastCommand extends ClanZSubCommand {
    public BroadcastCommand(ClansPlugin owner) {
        super("broadcast", owner);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
