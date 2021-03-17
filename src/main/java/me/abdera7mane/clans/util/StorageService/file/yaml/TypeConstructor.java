package me.abdera7mane.clans.util.StorageService.file.yaml;

import com.google.common.collect.Sets;

import me.abdera7mane.clans.types.*;
import me.abdera7mane.clans.ClansPlugin;
import me.ag.clans.types.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jetbrains.annotations.NotNull;

public class TypeConstructor extends Constructor {
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TypeConstructor() {
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructTypesMap());

    }

//    @Override
//    protected Object constructObject(Node node) {
//        final Map<?, ?> defaultConstruct = (Map<?, ?>) super.constructObject(node);
//
//        if (node.getTag() == Tag.MAP) {
//            for (ConstructTypes constructor : this.typeConstrcutors) {
//                final Set<String> keys = constructor.getRequiredKeys();
//
//                if (defaultConstruct.entrySet().containsAll(keys)) {
//                    return constructor.construct(node);
//                }
//            }
//        }
//
//        return defaultConstruct;
//    }

    private class ConstructTypesMap extends ConstructYamlMap {
        private final ConstructType<?>[] constructs = {new ConstructClan(), new ConstructPlayerConfig()};

        @Override
        public Object construct(Node node) {
            Object defaultConstruct = super.construct(node);
            Map<String, Object> map = simpleMap((Map<?,?>) defaultConstruct);
            for (ConstructType<?> construct : this.constructs) {
                if (map.keySet().containsAll(construct.getRequiredKeys())) {
                    return construct.constructFromMap(map);
                }
            }
            return defaultConstruct;
        }

        protected Map<String, Object> simpleMap(Map<?,?> map) {
            Map<String, Object> simpleMap = new LinkedHashMap<>(map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                simpleMap.put(entry.getKey().toString(), entry.getValue());
            }
            return simpleMap;
        }

    }

    private static abstract class ConstructType<T> {
        public abstract T constructFromMap(Map<String, Object> data);
        public abstract Set<String> getRequiredKeys();
    }

    private static class ConstructClan extends ConstructType<Clan> {

        @SuppressWarnings("unchecked")
        @Override
        public Clan constructFromMap(Map<String, Object> data) {
            String name  = (String) data.get("name");
            Date creationDate = (Date) data.get("creation-date");
            String leaderUUID = (String) data.get("leader");
            String description = (String) data.getOrDefault("description", "");
            int kills = (int) data.getOrDefault("kills", 0);
            int level = (int) data.getOrDefault("level", 0);


            ClanBuilder clanBuilder = new ClanBuilder(name, UUID.fromString(leaderUUID))
                    .setCreationDate(creationDate)
                    .setDescription(description)
                    .setKills(kills)
                    .setLevel(level);

            List<?> membersData = (List<?>) data.get("members");
            for (Object value : membersData) {
                Map<String, Object> map = (Map<String, Object>) value;
                String playerUUID = (String) map.get("uuid");
                Date joinDate = (Date) map.get("join-date");
                int memberkills = (int) map.getOrDefault("kills", 0);
                ClanRole role = ClanRole.valueOf((String) map.getOrDefault("role", "MEMBER"));

                ClanMemberBuilder memberBuilder = new ClanMemberBuilder(UUID.fromString(playerUUID))
                        .setJoinDate(joinDate)
                        .setKills(memberkills)
                        .setRole(role);

                clanBuilder.addMemberBuilder(memberBuilder);
            }

            return clanBuilder.build();
        }

        @NotNull
        @Override
        public Set<String> getRequiredKeys() {
            return Sets.newHashSet("name", "leader", "creation-date", "members");
        }
    }

    private static class ConstructPlayerConfig extends ConstructType<PlayerAdapter> {
        @SuppressWarnings("ConstantConditions")
        @Override
        public PlayerAdapter constructFromMap(Map<String, Object> data) {
            ClansPlugin plugin =  ClansPlugin.getInstance();

            String playerUUID = (String) data.get("uuid");
            String clan  = (String) data.get("clan");
            List<?> invitations = (List<?>) data.getOrDefault("invitations", "");

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

            PlayerAdapterBuilder builder = new PlayerAdapterBuilder(player)
                                           .setClan(plugin.getClan(clan));

            for (Object invitation : invitations) {
                String[] invitationData = invitation.toString().split(" ");
                String clanName = invitationData[0];
                String senderUUID = null;
                if (invitationData.length > 1){
                    senderUUID = invitationData[1];
                }
                builder.appendInvitation(new ClanInvitation(
                        plugin.getClan(clanName),
                        player,
                        senderUUID != null ? Bukkit.getOfflinePlayer(UUID.fromString(senderUUID)) : null
                ));

            }


            return builder.build();
        }

        @NotNull
        @Override
        public Set<String> getRequiredKeys() {
            return Sets.newHashSet("uuid");
        }
    }

}
