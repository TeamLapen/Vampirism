package de.teamlapen.lib.util.collections;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class SetView<ENTITY, COLLECTION extends Collection<ENTITY>> extends AbstractSet<ENTITY> {

    private final Map<?, COLLECTION> map;

    public SetView(Map<?, COLLECTION> map) {
        this.map = map;
    }

    @Override
    public @NotNull Iterator<ENTITY> iterator() {
        return this.map.values().stream().flatMap(Collection::stream).distinct().iterator();
    }

    @Override
    public int size() {
        return Math.toIntExact(this.map.values().stream().flatMap(Collection::stream).distinct().count());
    }
}
