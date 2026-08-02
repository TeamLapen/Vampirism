package de.teamlapen.faction.common.util.collections;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntComparator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CollectionUtil {

    /**
     * @implNote this only checks for updates in the keys, not the values. It assumes the values are immutable.
     */
    public static <TKey, TEntity> boolean updateCollection(Map<TKey, TEntity> oldMap, Map<TKey, TEntity> newMap, BiConsumer<TKey, TEntity> removeAction, BiConsumer<TKey, TEntity> addAction) {
        boolean changes = false;
        for (TKey tKey : new ArrayList<>(Sets.difference(oldMap.keySet(), newMap.keySet()))) {
            removeAction.accept(tKey, oldMap.get(tKey));
            oldMap.remove(tKey);
            changes = true;
        }

        for (TKey tKey : new ArrayList<>(Sets.difference(newMap.keySet(), oldMap.keySet()))) {
            oldMap.put(tKey, newMap.get(tKey));
            addAction.accept(tKey, newMap.get(tKey));
            changes = true;
        }

        oldMap.putAll(newMap);

        return changes;
    }

    public static <TKey, TEntity> boolean updateCollection(Map<TKey, TEntity> oldMap, Map<TKey, TEntity> newMap) {
        return updateCollection(oldMap, newMap, (k, v) -> {}, (k, v) -> {});
    }

    public static <TEntity> boolean updateCollection(Collection<TEntity> oldCollection, Collection<TEntity> newCollection) {
        return updateCollection(oldCollection, newCollection,_ -> {}, _ -> {});
    }

    /**
     * {@code oldCollection} is mutated in place: elements missing from {@code newCollection}
     * are removed from it, and elements missing from {@code oldCollection} are added to it.
     */
    public static <TEntity> boolean updateCollection(Collection<TEntity> oldCollection, Collection<TEntity> newCollection, Consumer<TEntity> removeAction, Consumer<TEntity> addAction) {
        boolean changed = false;
        for (TEntity entity : difference(oldCollection, newCollection)) {
            removeAction.accept(entity);
            oldCollection.remove(entity);
            changed = true;
        }

        for (TEntity entity : difference(newCollection, oldCollection)) {
            oldCollection.add(entity);
            addAction.accept(entity);
            changed = true;
        }

        return changed;
    }

    public static <TEntity> boolean checkCollection(Collection<TEntity> oldMap, Collection<TEntity> newMap) {
        return checkCollection(oldMap, newMap, _ -> {}, _ -> {});
    }

    public static <TEntity> boolean checkCollection(Collection<TEntity> oldCollection, Collection<TEntity> newCollection, Consumer<TEntity> removeAction, Consumer<TEntity> addAction) {
        boolean changed = false;
        for (TEntity entity : difference(oldCollection, newCollection)) {
            removeAction.accept(entity);
            changed = true;
        }

        for (TEntity entity : difference(newCollection, oldCollection)) {
            addAction.accept(entity);
            changed = true;
        }

        return changed;
    }

    /**
     * Computes the difference {@code first - second} lazily.
     * <p>
     * The result is an {@link Iterable} that snapshots the elements of {@code first} and the
     * membership information of {@code second} once, when this method is called. Afterward,
     * further mutation of {@code first} or {@code second} does not affect the returned iterable
     * nor invalidate its iterator (no {@link java.util.ConcurrentModificationException} risk),
     * and no intermediate result collection is eagerly materialized.
     */
    public static <T> Iterable<T> difference(Collection<T> first, Collection<T> second) {
        List<T> firstSnapshot = List.copyOf(first);
        Set<T> secondSnapshot = second instanceof Set<T> set ? Set.copyOf(set) : Set.copyOf(new HashSet<>(second));
        return () -> {
            Iterator<T> it = firstSnapshot.iterator();
            return new Iterator<>() {
                private T next;
                private boolean hasNext = advance();

                private boolean advance() {
                    while (it.hasNext()) {
                        T candidate = it.next();
                        if (!secondSnapshot.contains(candidate)) {
                            next = candidate;
                            return true;
                        }
                    }
                    next = null;
                    return false;
                }

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public T next() {
                    if (!hasNext) {
                        throw new NoSuchElementException();
                    }
                    T result = next;
                    hasNext = advance();
                    return result;
                }
            };
        };
    }

    public static class MapHashComparator<T,Z> implements Comparator<Map<T,Z>> {

        @Override
        public int compare(Map<T, Z> o1, Map<T, Z> o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    }

}
