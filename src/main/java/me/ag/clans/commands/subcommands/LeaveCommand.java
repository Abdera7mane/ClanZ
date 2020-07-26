package me.ag.clans.commands.subcommands;

import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanRole;
import me.ag.clans.util.PlayerUtilities;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaveCommand extends SubCommand {
    public LeaveCommand() {
        super("leave");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        Player player = (Player) sender;
        if (!PlayerUtilities.hasClan(player)) {
            player.sendMessage("you are not in any clan");
            return true;
        }
        else {
            Clan clan = PlayerUtilities.getPlayerClan(player);
            ClanRole role = clan.getMember(player).getRole();
            if (role != ClanRole.CAPTAIN) {
                clan.removeMember(player, Clan.LeaveReason.QUIT);
            }
            else {
                player.sendMessage("you can't leave the clan since you are the leader!");
            }
        }
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }
}
