package me.abdera7mane.clans.commands;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface SenderCheck {
    boolean check(CommandSender sender);
}
