package me.abdera7mane.clans.util;

import java.util.ArrayList;
import java.util.List;

public class LoadResult {

    private final List<String> warnnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public void appendWarrning(String warnning) {
        this.warnnings.add(warnning);
    }

    public void appendError(String error) {
        this.errors.add(error);
    }

    public boolean succeeded() {
        return this.errors.size() == 0;
    }

    public List<String> getWarnnings() {
        return this.warnnings;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
