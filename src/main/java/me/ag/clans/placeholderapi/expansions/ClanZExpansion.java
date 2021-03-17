package me.ag.clans.placeholderapi.expansions;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.Clan;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClanZExpansion extends PlaceholderExpansion implements Listener {

    private static final String VERSION = "1.0";

    private final ClansPlugin plugin;

    public ClanZExpansion(ClansPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "clanz";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){


        if(player == null){
            return null;
        }

        Clan clan = this.plugin.getPlayerClan(player);

        switch (identifier) {

            case "clan_name":
                return clan != null ? clan.getName() : "no clan";

            case "clan_description":
                return clan != null ? clan.getDescription() : "";
            case "clan_members_count":
                return clan != null ? String.valueOf(clan.getMembers(false).size()) : "0";
            case "clan_level":
                return clan != null ? String.valueOf(clan.getLevel()) : "0";
            case "clan_status":
                return clan != null ? clan.getStatus().name() : "";
        }

        return null;
    }

    @EventHandler
    private void onPluginDisabled(PluginDisableEvent event) {
        if (this.plugin.equals(event.getPlugin())) {
            this.unregister();
        }
    }

}
