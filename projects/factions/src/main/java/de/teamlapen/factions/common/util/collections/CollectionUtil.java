package de.teamlapen.factions.common.util.collections;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CollectionUtil {

    /**
     * @implNote this only checks for updates in the keys, not the values. It assumes the values are immutable.
     */
    public static <TKey, TEntity> void updateCollection(Map<TKey, TEntity> oldMap, Map<TKey, TEntity> newMap, BiConsumer<TKey, TEntity> removeAction, BiConsumer<TKey, TEntity> addAction) {
        for (TKey tKey : new ArrayList<>(Sets.difference(oldMap.keySet(), newMap.keySet()))) {
            removeAction.accept(tKey, oldMap.get(tKey));
            oldMap.remove(tKey);
        }

        for (TKey tKey : new ArrayList<>(Sets.difference(newMap.keySet(), oldMap.keySet()))) {
            oldMap.put(tKey, newMap.get(tKey));
            addAction.accept(tKey, newMap.get(tKey));
        }
    }

    public static <TEntity> boolean updateCollection(Set<TEntity> oldMap, Set<TEntity> newMap, Consumer<TEntity> removeAction, Consumer<TEntity> addAction) {
        boolean changed = false;
        for (TEntity tKey : new ArrayList<>(Sets.difference(oldMap, newMap))) {
            removeAction.accept(tKey);
            oldMap.remove(tKey);
            changed = true;
        }

        for (TEntity tKey : new ArrayList<>(Sets.difference(newMap, oldMap))) {
            oldMap.add(tKey);
            addAction.accept(tKey);
            changed = true;
        }
        return changed;
    }

    /**
     * @see #updateCollection(Map, Map, BiConsumer, BiConsumer)
     */
    public static <TKey, TEntity> void updateCollection(Map<TKey, TEntity> oldMap, Map<TKey, TEntity> newMap) {
        updateCollection(oldMap, newMap, (k, v) -> {}, (k, v) -> {});
    }

    public static <TEntity> boolean updateCollection(Set<TEntity> oldMap, Set<TEntity> newMap) {
        return updateCollection(oldMap, newMap, (v) -> {}, (v) -> {});
    }

    public static <TEntity> boolean checkCollection(Set<TEntity> oldMap, Set<TEntity> newMap) {
        return checkCollection(oldMap, newMap, (v) -> {}, (v) -> {});
    }

    public static <TEntity> boolean checkCollection(Set<TEntity> oldMap, Set<TEntity> newMap, Consumer<TEntity> removeAction, Consumer<TEntity> addAction) {
        boolean changed = false;
        for (TEntity entity : new ArrayList<>(Sets.difference(oldMap, newMap))) {
            removeAction.accept(entity);
            changed = true;
        }

        for (TEntity entity : new ArrayList<>(Sets.difference(newMap, oldMap))) {
            addAction.accept(entity);
            changed = true;
        }

        return changed;
    }

}
