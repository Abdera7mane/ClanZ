package me.abdera7mane.clans.util.StorageService.file.yaml;


import me.abdera7mane.clans.ClansPlugin;
import me.abdera7mane.clans.util.StorageService.file.FileStorageService;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class YAMLStorage extends FileStorageService {
    private final DumperOptions dumperOptions = new DumperOptions();
    private final Representer yamlRepresenter = new TypeRepresenter();
    private final Yaml yaml;

    public YAMLStorage(@NotNull ClansPlugin plugin) {
        super(plugin, "yaml");
        this.yaml = new Yaml(new TypeConstructor(), this.yamlRepresenter, this.dumperOptions);
    }

    @Override
    public void setup() {
        Pattern datePattern =  Pattern.compile("^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$"); // Just a placeholder
        this.yaml.addImplicitResolver(new Tag("!date"), datePattern, "0123");

        this.dumperOptions.setIndent(2);
        this.dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }

    @Override
    protected String dumpObject(@NotNull Object object) {
        return this.yaml.dump(object);
    }

    @Nullable
    @Override
    protected <T> T loadFromFile(File file, Class<T> type) {
        Object obj = null;
        String data = "";

        try {
            data = this.getContent(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!data.isEmpty()) {
            obj = this.yaml.loadAs(data, type);
        }

        return type.cast(obj);
    }
}
