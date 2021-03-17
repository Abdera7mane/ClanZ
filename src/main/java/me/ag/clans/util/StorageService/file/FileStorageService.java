package me.ag.clans.util.StorageService.file;

import com.google.common.base.Charsets;

import me.ag.clans.ClansPlugin;
import me.ag.clans.types.PlayerAdapter;
import me.ag.clans.types.Clan;
import me.ag.clans.util.StorageService.CacheStorageService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FileStorageService extends CacheStorageService {
    public final File PLAYERS_DIR;
    public final File CLANS_DIR;
    public final File DEFAULT_DIR;
    private final String implementation;
    private final Map<Class<?>, File> directoriesMap = new HashMap<>();

    protected FileStorageService(@NotNull ClansPlugin plugin, @NotNull String implementation) {
        super(plugin);

        this.implementation = implementation;

        final File dataFolder = new File(plugin.getDataFolder(), "data" + File.separator + getImplementation());

        this.PLAYERS_DIR = new File(dataFolder, "players");
        this.CLANS_DIR = new File(dataFolder, "clans");
        this.DEFAULT_DIR = new File(dataFolder, "other");

        this.directoriesMap.put(PlayerAdapter.class, PLAYERS_DIR);
        this.directoriesMap.put(Clan.class, CLANS_DIR);

    }

    @Override
    public void storePlayerAdapter(@NotNull PlayerAdapter configuration) {
        super.storePlayerAdapter(configuration);
        this.store(configuration);
    }

    @Override
    public void storeClan(@NotNull Clan clan) {
        super.storeClan(clan);
        this.store(clan);
    }

    private void store(@NotNull Object object) {
        try {
            this.saveToFile(object, getDirectoriesMap().getOrDefault(object.getClass(), DEFAULT_DIR));
        } catch (Exception e) {
            this.getPlugin().log("Failed to save " + object.getClass().getSimpleName(), Level.SEVERE);
            e.printStackTrace();
        }
    }

    @Override
    protected @Nullable PlayerAdapter findPlayerAdapter(@NotNull OfflinePlayer player) {
        final File file = new File(PLAYERS_DIR, File.separator + player.getUniqueId().toString() + ".yml");
        return loadFromFile(file, PlayerAdapter.class);
    }

    @Nullable
    @Override
    protected Clan findClan(@NotNull String name) {
        final File file = new File(CLANS_DIR, File.separator + name.toLowerCase() + ".yml");
        return loadFromFile(file, Clan.class);
    }
    
    public String getImplementation() {
        return this.implementation;
    }

    public Map<Class<?>, File> getDirectoriesMap() {
        return this.directoriesMap;
    }

    protected String getContent(File file) throws IOException {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void saveToFile(@NotNull Object object, @NotNull File file) throws Exception {
        file.getParentFile().mkdirs();

        String data = dumpObject(object);


        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
            writer.write(data);
        }
    }

    protected abstract String dumpObject(@NotNull Object object);

    @Nullable
    protected abstract <T> T loadFromFile(File file, Class<T> type);
}
