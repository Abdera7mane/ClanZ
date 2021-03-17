package me.ag.clans;

import me.ag.clans.types.Clan;
import me.ag.clans.util.ClanUtilities;

import me.ag.clans.util.StorageService.file.yaml.TypeConstructor;
import me.ag.clans.util.StorageService.file.yaml.TypeRepresenter;

import org.bukkit.OfflinePlayer;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClanUtilities.class)
public class ClansPluginTest {

    @Test
    public void saveTest() {
//        OfflinePlayer player = mock(OfflinePlayer.class);
//        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
//        Clan clan = new Clan("test", player);
//
//        DumperOptions dumperOptions = new DumperOptions();
//        Representer yamlRepresenter = new TypeRepresenter();
//        dumperOptions.setIndent(2);
//        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//        Yaml yaml = new Yaml(new TypeConstructor(), yamlRepresenter, dumperOptions);
//        yaml.addImplicitResolver(new Tag("!clan"), Resolver.VALUE, "");
//
//        String output = yaml.dump(clan);
//        System.out.println(output);
//        Object input = yaml.load(output);
//        System.out.println(input);
    }
}