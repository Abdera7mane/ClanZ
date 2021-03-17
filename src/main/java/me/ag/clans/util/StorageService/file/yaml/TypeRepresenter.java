package me.ag.clans.util.StorageService.file.yaml;

import me.ag.clans.types.Clan;
import me.ag.clans.types.ClanMember;

import me.ag.clans.types.PlayerAdapter;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeRepresenter extends Representer {
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TypeRepresenter() {
        this.representers.put(Clan.class, new RepresentClan());
        this.representers.put(ClanMember.class, new RepresentClanMember());
        this.representers.put(PlayerAdapter.class, new RepresentPlayerConfig());
        this.representers.put(Date.class, new RepresentDate());
    }

    private class RepresentClan implements Represent {

        @Override
        public Node representData(Object data) {
            final Clan clan = (Clan) data;
            final Map<String, Object> config = new LinkedHashMap<>();

            config.put("name", clan.getName());
            config.put("description", clan.getDescription());
            config.put("creation-date", clan.getCreationDate());
            config.put("leader", clan.getLeader().getOfflinePlayer().getUniqueId().toString());
            config.put("status", clan.getStatus().toString());
            config.put("kills", clan.getKills());
            config.put("level", clan.getLevel());
            config.put("members", clan.getMembers(false));

            return TypeRepresenter.this.representData(config);
        }
    }

    private class RepresentClanMember implements Represent {

        @Override
        public Node representData(Object data) {
            System.out.println("Called");
            final ClanMember member = (ClanMember) data;
            final Map<String, Object> config = new LinkedHashMap<>();

            config.put("uuid", member.getOfflinePlayer().getUniqueId().toString());
            config.put("join-date", member.getJoinDate());
            config.put("role", member.getRole().toString());
            config.put("kills", member.getKills());

            return TypeRepresenter.this.representData(config);
        }
    }

    private class RepresentPlayerConfig implements Represent {

        @Override
        public Node representData(Object data) {
            final PlayerAdapter adapter = (PlayerAdapter) data;
            final Map<String, Object> config = new LinkedHashMap<>();

            final Clan clan = adapter.getClan();

            config.put("uuid", adapter.self().getUniqueId().toString());
            config.put("clan", clan != null ? clan.getName() : null);

            return TypeRepresenter.this.representData(config);
        }
    }

    private class RepresentDate implements Represent {

        @Override
        public Node representData(Object data) {
            Date date = (Date) data;
            String value = dateFormat.format(date);

            return TypeRepresenter.this.representData(value);
        }
    }
}
