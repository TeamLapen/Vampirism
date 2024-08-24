package de.teamlapen.lib.lib.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LiveMap<U> extends AbstractSet<U> {

    private final Map<?, List<U>> map;

    public LiveMap(Map<?,List<U>> map) {
        this.map = map;
    }

    @Override
    public @NotNull Iterator<U> iterator() {
        return this.map.values().stream().flatMap(Collection::stream).distinct().iterator();
    }

    @Override
    public int size() {
        return (int) this.map.values().stream().flatMap(Collection::stream).distinct().count();
    }

}
