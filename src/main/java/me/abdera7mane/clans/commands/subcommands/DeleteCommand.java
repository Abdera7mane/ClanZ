package me.abdera7mane.clans.commands.subcommands;

import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.messages.formatter.Formatter;
import me.abdera7mane.clans.messages.formatter.PlayerFormatter;
import me.abdera7mane.clans.ClansPlugin;

import static me.abdera7mane.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.abdera7mane.clans.commands.SenderRequirement.WITH_CLAN;

import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.types.ClanRole;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITH_CLAN})
public final class DeleteCommand extends ClanZSubCommand {

    public DeleteCommand(ClansPlugin owner) {
        super("delete", owner);
        setPermission("clanz.delete");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        final Player player = (Player) sender;
        final Clan clan = this.getPlugin().getPlayerClan(player);
        final ClanRole role = clan.getMember(player).getRole();

        final Formatter[] formatters = {new PlayerFormatter(player), clan};

        Messages.MessageKey messageKey;

        if (role == ClanRole.LEADER) {
            clan.delete();
            messageKey = Messages.Globals.CLAN_DELETED;
        }
        else {
            messageKey = Messages.Errors.NO_CLAN_PERMISSION;
        }

        this.getMessages().sendMessage(
                messageKey,
                player,
                formatters
        );

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
