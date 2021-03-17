package me.abdera7mane.clans.commands.subcommands;

import java.util.List;

import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.messages.formatter.Formatter;
import me.abdera7mane.clans.messages.formatter.PlayerFormatter;
import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.types.Clan;
import static me.abdera7mane.clans.commands.SenderRequirement.PLAYER_ONLY;
import static me.abdera7mane.clans.commands.SenderRequirement.WITHOUT_CLAN;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITHOUT_CLAN})
public final class JoinCommand extends ClanZSubCommand {

    public JoinCommand(ClansPlugin owner) {
        super("join", owner);
        setPermission("clanz.join");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        
        final Player player = (Player) sender;
        final Formatter playerFormatter = new PlayerFormatter(player);

        Clan currentClan = this.getPlugin().getPlayerClan(player);
        if (currentClan != null) {
            this.getMessages().sendMessage(
                    Messages.Errors.ALREADY_HAVE_CLAN,
                    player,
                    playerFormatter, currentClan
            );
            return true;
        }

        else if (args.length < 2) {
            return false;
        }

        String clanName = args[1];
        Clan clan = this.getPlugin().getClan(clanName);
        if (clan != null) {
            Messages.MessageKey message = null;
            switch (clan.getStatus()) {
                case PUBLIC:
                    clan.addMember(player);
                    break;
                case INVITE_ONLY:
                    message = Messages.Globals.INVITATION_SENT;
                    break;
                case CLOSED:
                    message = Messages.Globals.CLAN_CLOSED;
                    break;
            }
            if (message != null)
                this.getMessages().sendMessage(
                        message,
                        player,
                        playerFormatter, clan
                );
            return true;

        }
        else {
            this.getMessages().sendMessage(Messages.Errors.CLAN_NOT_FOUND, player, playerFormatter);
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
