package me.ag.clans.commands;

import me.ag.clans.Clans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {

    Clans plugin = Clans.getPlugin(Clans.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return true;

        plugin.getServer().getConsoleSender().sendMessage("CLAANS");
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sender.sendMessage("§echoose a name for the clan");
                return false;
            }
            System.out.println("creating ...");
            if (!plugin.clanExists(args[1])) {
                boolean success = plugin.createClan(args[1], (Player) sender);
               System.out.println(success);
               return true;
            }
            sender.sendMessage("This clan name is already taken");
        }

        else if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
        }
        else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            plugin.reloadConfig();
            System.out.println("&2Config reloaded !");
        }
        else if (args[0].equalsIgnoreCase("test")) {
            System.out.println(plugin.getConfig().get("max-name-length"));
        }

        return true;
    }
    static void sendHelp(CommandSender p) {
        p.sendMessage("this is the help section");
    }

}