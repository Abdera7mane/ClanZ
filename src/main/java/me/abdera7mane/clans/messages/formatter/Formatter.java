package me.abdera7mane.clans.messages.formatter;

import org.jetbrains.annotations.NotNull;

public interface Formatter {

    @NotNull
    String format(@NotNull String message);
}
