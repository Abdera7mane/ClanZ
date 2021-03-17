package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
import static me.ag.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.ag.clans.commands.SenderRequirement.WITH_CLAN;
import static me.ag.clans.messages.Messages.Globals.*;
import static me.ag.clans.messages.Messages.Errors.*;
import me.ag.clans.messages.formatter.Formatter;
import me.ag.clans.messages.formatter.PlayerFormatter;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanRole;

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
            messageKey = CLAN_DELETED;
        }
        else {
            messageKey = NO_CLAN_PERMISSION;
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
