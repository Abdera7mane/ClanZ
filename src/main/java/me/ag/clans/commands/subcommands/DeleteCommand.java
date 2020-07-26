package me.ag.clans.commands.subcommands;

import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanRole;
import me.ag.clans.util.PlayerUtilities;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class DeleteCommand extends SubCommand {
    public DeleteCommand() {
        super("delete");
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
            if (role == ClanRole.CAPTAIN) {
                clan.delete();
            }
            else {
                player.sendMessage("no enough permissions to do that");
            }
        }
        return false;
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }
}
