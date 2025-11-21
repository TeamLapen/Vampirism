package de.teamlapen.lib.util;

public class IntReference {

    private int value;

    public IntReference() {
        this(0);
    }

    public IntReference(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    public void add(int value) {
        this.value += value;
    }
}
