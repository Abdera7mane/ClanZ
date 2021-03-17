package me.ag.clans.commands.subcommands;

import java.util.List;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.formatter.Formatter;
import me.ag.clans.messages.formatter.PlayerFormatter;
import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;
import me.ag.clans.types.ClanRole;
import static me.ag.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.ag.clans.commands.SenderRequirement.WITH_CLAN;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITH_CLAN})
public final class PromoteCommand extends ClanZSubCommand{

    public PromoteCommand(ClansPlugin owner) {
        super("promote", owner);
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
                    targetMember.promote();
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
