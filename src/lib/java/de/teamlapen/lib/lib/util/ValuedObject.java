package de.teamlapen.lib.lib.util;


import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Comparator;

@SuppressWarnings("ClassCanBeRecord")
public class ValuedObject<T> {
    public static <Q> Comparator<ValuedObject<Q>> getComparator() {
        return Comparator.comparingInt(qValuedObject -> qValuedObject.value);
    }

    public static <Q> @NotNull Comparator<ValuedObject<Q>> getInvertedComparator() {
        return (qValuedObject, t1) -> t1.value - qValuedObject.value;
    }

    /**
     * Extract the objects of the ValuedObjects out of the given array into a new array
     */
    public static <Q> Q @NotNull [] extract(Class<Q> clz, ValuedObject<Q> @NotNull [] array) {
        @SuppressWarnings("unchecked")
        Q[] a = (Q[]) Array.newInstance(clz, array.length);
        for (int i = 0; i < array.length; i++) {
            a[i] = array[i].object;
        }
        return a;
    }

    public final T object;
    public final int value;

    public ValuedObject(T object, int value) {
        this.object = object;
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        return "Obj " + object + " Value " + value;
    }
}
