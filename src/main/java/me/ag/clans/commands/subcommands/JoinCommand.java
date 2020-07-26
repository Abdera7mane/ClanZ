package me.ag.clans.commands.subcommands;

import me.ag.clans.ClansPlugin;
import me.ag.clans.messages.Messages;
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
            player.sendMessage("you already have a clan");
            return true;
        }
        else if (args.length < 2) {
              sender.sendMessage("supply a clan");
              return true;
        }
        String clanName = args[1];
        Clan clan = ClanUtilities.getClan(clanName);
        if (clan != null) {
            switch (clan.getStatus()) {
                case PUBLIC:
                    clan.addMember(player, false);
                    break;
                case INVITE_ONLY:
                    sender.sendMessage("invitation sent to clan: " + clan.getName());
                    break;
                case CLOSED:
                    sender.sendMessage("this clan is closed");
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
}
