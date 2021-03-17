package me.abdera7mane.clans.util.StorageService;

import me.abdera7mane.clans.types.Clan;
import me.abdera7mane.clans.types.PlayerAdapter;
import me.abdera7mane.clans.Cache;
import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.MemoryCache;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CacheStorageService extends AbstractStorageService {
    public static final long CACHE_EXPIRY = 60_000L;

    private final MemoryCache<UUID, PlayerAdapter> playersCache = new Cache<>(CACHE_EXPIRY);
    private final MemoryCache<String, Clan> clansCache = new Cache<>(CACHE_EXPIRY);

    public CacheStorageService(@NotNull ClansPlugin plugin) {
        super(plugin);
    }

    protected MemoryCache<UUID, PlayerAdapter> getPlayersCache() {
        return this.playersCache;
    }

    protected MemoryCache<String, Clan> getClansCache() {
        return this.clansCache;
    }

    @Nullable
    @Override
    public final PlayerAdapter getPlayerAdapter(@NotNull OfflinePlayer player) {
        if (!this.isPlayerCached(player)) {
            this.loadPlayerConfiugration(player);
        }

        return this.getPlayerAdapterFromCache(player);
    }

    @Nullable
    @Override
    public final Clan getClan(@NotNull String name) {
        String lower = name.toLowerCase();
        if (!this.isClanCached(lower)) {
            this.loadClan(lower);
        }

        return this.getClanFromCache(lower);
    }

    @Override
    public void storePlayerAdapter(@NotNull PlayerAdapter adapter) {
        this.getPlayersCache().put(adapter.self().getUniqueId(), adapter);
    }

    @Override
    public void storeClan(@NotNull Clan clan) {
        this.getClansCache().put(clan.getName().toLowerCase(), clan);
    }

    protected final @Nullable PlayerAdapter getPlayerAdapterFromCache(@NotNull OfflinePlayer player) {
        return this.getPlayersCache().get(player.getUniqueId());
    }

    @Nullable
    protected final Clan getClanFromCache(@NotNull String name) {
        return this.getClansCache().get(name.toLowerCase());
    }

    protected final boolean isPlayerCached(@NotNull OfflinePlayer player) {
        return this.getPlayersCache().containsKey(player.getUniqueId());
    }

    protected final boolean isClanCached(@NotNull String name) {
        return this.getClansCache().containsKey(name.toLowerCase());
    }

    public void loadPlayerConfiugration(@NotNull OfflinePlayer player) {
        if (this.isPlayerCached(player)) {
            return;
        }

        PlayerAdapter adapter = this.findPlayerAdapter(player);
        this.cachePlayerAdapter(adapter);
    }

    public void loadClan(@NotNull String name) {
        if (this.isClanCached(name)) {
            return;
        }

        Clan clan = this.findClan(name.toLowerCase());
        this.cacheClan(clan);
    }

    protected void cachePlayerAdapter(PlayerAdapter adapter) {
        if (adapter != null) {
            OfflinePlayer player = adapter.self();
            UUID uuid = player.getUniqueId();
            this.getPlayersCache().put(uuid, adapter, !player.isOnline());
        }
    }

    protected void cacheClan(Clan clan) {
        if (clan != null) {
            String keyName = clan.getName().toLowerCase();
            boolean expire = clan.getMembers(true).size() > 0;
            this.getClansCache().put(keyName, clan, expire);
        }
    }

    public final void saveClansCache() {
        for (Clan clan : this.getClansCache().values()) {
            this.storeClan(clan);
        }
    }

    public final void savePlayersCache() {
        for (PlayerAdapter adapter : this.getPlayersCache().values()) {
            this.storePlayerAdapter(adapter);
        }
    }

    public final void saveAll() {
        this.savePlayersCache();
        this.saveClansCache();
    }

    @Override
    public void stop() {
        this.getPlayersCache().clear();
        this.getClansCache().clear();
    }

    protected abstract @Nullable PlayerAdapter findPlayerAdapter(@NotNull OfflinePlayer player);

    @Nullable
    protected abstract Clan findClan(@NotNull String name);

}
