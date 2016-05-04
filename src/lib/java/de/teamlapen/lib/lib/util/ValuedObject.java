package de.teamlapen.lib.lib.util;


import java.lang.reflect.Array;
import java.util.Comparator;

public class ValuedObject<T> {
    public static <Q> Comparator<ValuedObject<Q>> getComparator() {
        return new Comparator<ValuedObject<Q>>() {

            @Override
            public int compare(ValuedObject<Q> qValuedObject, ValuedObject<Q> t1) {
                return qValuedObject.value - t1.value;
            }
        };
    }

    public static <Q> Comparator<ValuedObject<Q>> getInvertedComparator() {
        return new Comparator<ValuedObject<Q>>() {

            @Override
            public int compare(ValuedObject<Q> qValuedObject, ValuedObject<Q> t1) {
                return t1.value - qValuedObject.value;
            }
        };
    }

    /**
     * Extract the objects of the ValuedObjects out of the given array into a new array
     *
     * @param clz
     * @param array
     * @param <Q>
     * @return
     */
    public static <Q> Q[] extract(Class<Q> clz, ValuedObject<Q>[] array) {
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
    public String toString() {
        return "Obj " + object + " Value " + value;
    }
}
