package me.ag.clans.util.StorageService;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.PlayerAdapter;
import me.ag.clans.types.Clan;

import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStorageService {
    private final ClansPlugin plugin;

    protected AbstractStorageService(@NotNull ClansPlugin plugin) {
        this.plugin = plugin;
    }

    protected final ClansPlugin getPlugin() {
        return this.plugin;
    }

    public abstract void setup();

    public abstract void storePlayerAdapter(@NotNull PlayerAdapter configuration);

    public abstract void storeClan(@NotNull Clan clan);

    @Nullable
    public abstract PlayerAdapter getPlayerAdapter(@NotNull OfflinePlayer player);

    @Nullable
    public abstract Clan getClan(@NotNull String name);

    public abstract void stop();
}
