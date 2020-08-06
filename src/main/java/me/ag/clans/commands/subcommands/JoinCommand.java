package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.formatter.ClanFormatter;
import me.ag.clans.messages.formatter.Formatter;
import me.ag.clans.messages.formatter.PlayerFormatter;
import me.ag.clans.types.Clan;
import me.ag.clans.util.ClanUtilities;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class JoinCommand extends SubCommand {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public JoinCommand() {
        super("join");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        Player player = (Player) sender;
        Messages messages = plugin.getMessages();

        if (PlayerUtilities.hasClan(player)) {
            Clan clan = PlayerUtilities.getPlayerClan(player);
            String message = messages.getErrorMessage(Messages.Errors.ALREADY_HAVE_CLAN, new PlayerFormatter(player), new ClanFormatter(clan));
            player.sendMessage(message);
            return true;
        }
        else if (args.length < 2) {
              sender.sendMessage("supply a clan");
              return true;
        }
        String clanName = args[1];
        Clan clan = ClanUtilities.getClan(clanName);
        if (clan != null) {
            Formatter[] formatters = {new PlayerFormatter(player), new ClanFormatter(clan)};
            String message;
            switch (clan.getStatus()) {
                case PUBLIC:
                    clan.addMember(player, false);
                    break;
                case INVITE_ONLY:
                    message = messages.getMessage(Messages.Global.INVITATION_SENT, formatters);
                    sender.sendMessage(message);
                    break;
                case CLOSED:
                    message = messages.getMessage(Messages.Global.CLAN_CLOSED, formatters);
                    sender.sendMessage(message);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    @Override
    public boolean clanRequired() {
        return false;
    }
}
