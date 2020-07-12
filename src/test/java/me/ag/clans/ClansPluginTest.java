package me.ag.clans;

import me.ag.clans.types.Clan;
import me.ag.clans.util.ClanUtilities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClanUtilities.class)
public class ClansPluginTest {
    private static Map<String, Clan> loadedClans = new HashMap<>();

    @Test
    public void clanCreate() {
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("Abdera7mane");
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        ClanUtilities.createClan("hello", mockPlayer);

    }

    public ClansPluginTest getInstance() {
        return this;
    }

    public HashMap<String, Clan> getLoadedClans() {
        return (HashMap<String, Clan>) loadedClans;
    }

    public void loadClan(Clan clan) {
        loadedClans.put(clan.getName(), clan);
    }
    public void unloadAll() {
        loadedClans.clear();
    }

    public void unloadClan(String clan) {
        loadedClans.remove(clan);
    }

}