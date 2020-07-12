package me.ag.clans.commands;

import me.ag.clans.types.Clan;
import me.ag.clans.util.ClanUtilities;
import me.ag.clans.ClansPlugin;

import me.ag.clans.util.PlayerUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ClanCommand extends Command {

    private ClansPlugin plugin = ClansPlugin.getInstance();
    private static final String[] PLAYER_COMMANDS = {"broadcast", "chat", "create", "demote", "delete", "join", "kick", "leave", "promote"};
    private static final String[] CLAN_COMMANDS = {"broadcast", "chat", "demote", "delete", "kick", "leave", "promote"};

    public ClanCommand() {
        super("clan");
        setDescription("The main Clans command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        String arg0 = args[0].toLowerCase();
        if (Arrays.asList(PLAYER_COMMANDS).contains(arg0) && sender instanceof ConsoleCommandSender) {
            sender.sendMessage("§cThis command is executable only by players.");
            return true;
        }

        if (Arrays.asList(CLAN_COMMANDS).contains(arg0) && sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtilities.getPlayerClan(player) == null) {
                player.sendMessage("§cYou are not in any clan");
                return true;
            }

        }


        switch (arg0) {
            case "create":
                if (PlayerUtilities.getPlayerClan((Player) sender) != null) {
                    sender.sendMessage("§7you already have a clan");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§eUsage: §f/clan create §4[name]§r");
                    return true;
                }
                {
                    Player player = (Player) sender;
                    String clanName = args[1];
                    if (ClanUtilities.getClan(clanName) == null) {
                        boolean success = ClanUtilities.createClan(clanName, player);
                        if (!success) {
                            player.sendMessage("§4Couldn't create the clan !");
                        } else {
                            player.sendMessage("§2Clan created ! YAY");
                        }
                        return true;
                    }
                    sender.sendMessage("§cThis clan already exists.");
                }
                break;
            case "join":
                if (PlayerUtilities.getPlayerClan((Player) sender) != null) {
                    sender.sendMessage("§7you already have a clan");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§7supply a Clan");
                    return true;
                }
                {
                    Player player = (Player) sender;
                    String clanName = args[1];
                    Clan clanInstance = ClanUtilities.getClan(clanName);
                    if (clanInstance == null) {
                        player.sendMessage("§cCouldn't find a clan named §r" + clanName);
                        return true;
                    }

                    else if (clanInstance.isOpen()) {
                        clanInstance.addMember(player);
                    } else player.sendMessage("§cThis clan is closed.");
                }
                break;

            case "leave":
                {
                    Player player = (Player) sender;
                    Clan clan = PlayerUtilities.getPlayerClan(player);
                    if (clan != null) {
                       clan.removeMember(player, Clan.LeaveReason.QUIT);
                    } else player.sendMessage("§cYou don't have a clan !");
                }
                break;

            case "reload":
                plugin.reloadConfig();
                ClansPlugin.log("§2Config reloaded !");
                break;

            case "test":
                ClansPlugin.log(plugin.getConfig().saveToString());
                break;

            default:
                sender.sendMessage("§7Type /clan help");
                break;
        }

        return true;
    }
    private void sendHelp(CommandSender p) {
        p.sendMessage("this is the help section");
    }

}