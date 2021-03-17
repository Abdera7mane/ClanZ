package me.abdera7mane.clans.commands.subcommands;

import java.util.List;

import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.messages.formatter.Formatter;
import me.abdera7mane.clans.messages.formatter.PlayerFormatter;
import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.types.ClanMember;
import me.abdera7mane.clans.types.ClanRole;
import static me.abdera7mane.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.abdera7mane.clans.commands.SenderRequirement.WITH_CLAN;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITH_CLAN})
public final class DemoteCommand extends ClanZSubCommand {

    public DemoteCommand(ClansPlugin owner) {
        super("demote", owner);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        final Player player = (Player) sender;
        final Clan clan = this.getPlugin().getPlayerClan(player);
        final ClanRole role = clan.getMember(player).getRole();

        final Formatter[] formatters = {new PlayerFormatter(player), clan};

        if (role.compareTo(ClanRole.CAPTAIN) < 0) {
            this.getMessages().sendMessage(
                    Messages.Errors.NO_CLAN_PERMISSION,
                    player,
                    formatters
            );
        }
        else if (args.length > 1) {
            OfflinePlayer target = clan.getOfflinePlayer(args[0]);
            if (target != null) {
                ClanMember targetMember = clan.getMember(target);
                if (role.compareTo(targetMember.getRole()) > 0){
                    targetMember.demote();
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
