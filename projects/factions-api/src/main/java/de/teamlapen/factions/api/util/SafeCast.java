package de.teamlapen.factions.api.util;

public class SafeCast {

    /**
     * Makes an unchecked cast to the target type. You must be sure that the cast is safe.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }
}
