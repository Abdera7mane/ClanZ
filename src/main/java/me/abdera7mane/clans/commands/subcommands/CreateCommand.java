package me.abdera7mane.clans.commands.subcommands;

import java.util.List;

import me.abdera7mane.clans.messages.Messages;
import me.abdera7mane.clans.messages.formatter.Formatter;
import me.abdera7mane.clans.messages.formatter.PlayerFormatter;
import me.abdera7mane.clans.ClansPlugin;
import static me.abdera7mane.clans.commands.SenderRequirement.*;

import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.util.ClanUtilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions(requirements = {PLAYER_ONLY, WITHOUT_CLAN})
public final class CreateCommand extends ClanZSubCommand {

    public CreateCommand(ClansPlugin owner) {
        super("create", owner);
        setPermission("clanz.create");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        final Player player = (Player) sender;
        final Formatter playerFormmater = new PlayerFormatter(player);

        if (this.getPlugin().PlayerHasClan(player)) {
            Clan clan = this.getPlugin().getPlayerClan(player);
            this.getMessages().sendMessage(
                    Messages.Errors.ALREADY_HAVE_CLAN,
                    player,
                    new PlayerFormatter(player), clan
            );
            return true;
        }

        if (args.length > 1) {
            String clanName = args[1];
            if (!ClanUtilities.isValidClanName(clanName)) {
                this.getMessages().sendMessage(
                        Messages.Errors.INVALID_CLAN_NAME,
                        player,
                        new PlayerFormatter(player)
                );
            }

            else if (args.length > 2) {
                this.getMessages().sendMessage(
                        Messages.Errors.NO_SPACES_IN_CLAN_NAME,
                        player,
                        playerFormmater
                );
            }
            else if (this.getPlugin().clanExists(clanName)) {
                Clan clan = this.getPlugin().getClan(clanName);
                this.getMessages().sendMessage(
                        Messages.Errors.CLAN_EXISTS,
                        player,
                        playerFormmater, clan
                );
            }

            Clan createdClan = this.getPlugin().createClan(clanName, player);
            if (createdClan != null) {
                this.getMessages().sendMessage(
                        Messages.Globals.CLAN_CREATED,
                        player,
                        playerFormmater, createdClan
                );
            }
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
