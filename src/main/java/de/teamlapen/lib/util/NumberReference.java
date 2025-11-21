package de.teamlapen.lib.util;

public class NumberReference<T extends Number> {

    private T value;

    public NumberReference() {
    }

    public NumberReference(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
