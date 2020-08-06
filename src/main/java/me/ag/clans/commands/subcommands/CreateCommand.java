package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
import me.ag.clans.messages.formatter.ClanFormatter;
import me.ag.clans.messages.formatter.PlayerFormatter;
import me.ag.clans.types.Clan;
import me.ag.clans.util.ClanUtilities;
import me.ag.clans.util.InvalidClanNameException;
import me.ag.clans.util.PlayerUtilities;

import org.apache.commons.lang.ArrayUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class CreateCommand extends SubCommand {
    private static final ClansPlugin plugin = ClansPlugin.getInstance();

    public CreateCommand() {
        super("create");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        final Player player = (Player) sender;
        final Messages messages = plugin.getMessages();

        if (PlayerUtilities.hasClan(player)) {
            Clan clan = PlayerUtilities.getPlayerClan(player);
            String message = messages.getErrorMessage(Messages.Errors.ALREADY_HAVE_CLAN, new PlayerFormatter(player), new ClanFormatter(clan));
            player.sendMessage(message);
            return true;
        }

        if (args.length > 1) {
            String clanName = args[1];
            if (!ClanUtilities.isValidClanName(clanName)) {
                String message = messages.getErrorMessage(Messages.Errors.INVALID_CLAN_NAME, new PlayerFormatter(player));
                player.sendMessage(message);
            }

            else if (args.length > 2) {
                player.sendMessage("You can't have spaces in clan name");
                String fullName = String.join("_", (String[]) ArrayUtils.subarray(args, 1, args.length)) ;
                player.sendMessage("TIP!: try with " + fullName );
            }
            else if (ClanUtilities.clanExist(clanName)) {
                Clan clan = ClanUtilities.getClan(clanName);
                String message = messages.getErrorMessage(Messages.Errors.CLAN_EXISTS, new PlayerFormatter(player), new ClanFormatter(clan));
                player.sendMessage(message);
            }

            else {
                try {
                    boolean success = ClanUtilities.createClan(clanName, player);
                    if (success) {
                        Clan createdClan = ClanUtilities.getClan(clanName);
                        String message = messages.getMessage(Messages.Global.CLAN_CREATED, new PlayerFormatter(player), new ClanFormatter(createdClan));
                        player.sendMessage(message);
                    }
                } catch (InvalidClanNameException ignored) {}
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    @Override
    public boolean clanRequired() {
        return true;
    }
}
