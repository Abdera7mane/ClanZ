package me.abdera7mane.clans.commands.subcommands;

import me.abdera7mane.clans.ClansPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.abdera7mane.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.abdera7mane.clans.commands.SenderRequirement.WITH_CLAN;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITH_CLAN})
public final class LeaveCommand extends ClanZSubCommand {
    public LeaveCommand(ClansPlugin owner) {
        super("leave", owner);
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
