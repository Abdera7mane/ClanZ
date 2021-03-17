package me.abdera7mane.clans.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class CommentedYamlConfiguration extends YamlConfiguration {
    protected static final String COMMENT_PREFIX = "#";

    Map<Integer, String> comments = new HashMap<>();

    @Override
    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        try{
            super.loadFromString(contents);
        } finally {
            String[] lines = contents.split(System.lineSeparator());
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].replaceAll("\\s+$", "");
                if (line.trim().startsWith(COMMENT_PREFIX) || line.isEmpty()) {
                    comments.put(i, line);
                }
            }
        }
    }

    @NotNull
    @Override
    public String saveToString() {
        String data = super.saveToString();
        StringBuilder stringBuilder = new StringBuilder();

        int currentLine = 0;
        for (String line : data.split(System.lineSeparator())) {
            while (comments.containsKey(currentLine)) {
                stringBuilder.append(comments.get(currentLine))
                        .append(System.lineSeparator());
                currentLine++;
            }
            stringBuilder.append(line)
                    .append(System.lineSeparator());
            currentLine++;
        }

        return stringBuilder.toString();
    }
}
