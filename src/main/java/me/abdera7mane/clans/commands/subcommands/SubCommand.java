package me.abdera7mane.clans.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import me.abdera7mane.clans.ClansPlugin;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SubCommandOptions()
public abstract class SubCommand implements TabCompleter, Comparable<SubCommand> {

    private final String label;
    private final JavaPlugin owner;
    private List<String> aliases;
    private String description;
    private String permission;

    public SubCommand(@NotNull String label, @NotNull JavaPlugin owner) {
        this(label, owner, new ArrayList<>());
    }

    public SubCommand(@NotNull String label, @NotNull JavaPlugin owner, @NotNull List<String> aliases) {
        this.label = label.toLowerCase();
        this.owner = owner;
        this.setAliases(aliases);
        this.setDescription(((ClansPlugin) getOwner()).getMessages().getSubCommandDescription(this));
    }

    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args);

    @NotNull
    public String getLabel() {
        return this.label;
    }

    @NotNull
    public JavaPlugin getOwner() {
        return this.owner;
    }

    @NotNull
    public String getDescription() {
        return this.description;
    }

    @NotNull
    public String getUsage() {
        return ((ClansPlugin) getOwner()).getMessages().getSubCommandUsage(this);
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @Nullable
    public String getPermission() {
        return this.permission;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        SubCommand that = (SubCommand) o;

        if (!label.equals(that.label)) return false;
        return this.owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        int hash = this.getLabel().hashCode();
        hash = 31 * hash + this.getOwner().hashCode();
        return hash;
    }

    @Override
    public final int compareTo(@NotNull SubCommand command) {
        return this.getLabel().compareTo(command.getLabel());
    }
}
