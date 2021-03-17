package me.abdera7mane.clans;

import me.abdera7mane.clans.util.ClanUtilities;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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