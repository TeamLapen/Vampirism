package de.teamlapen.vampirism.common.util;

import java.util.stream.Stream;

public class StreamUtil {

    @SafeVarargs
    public static <T> Stream<T> append(Stream<T> stream, T... element) {
        return Stream.concat(stream, Stream.of(element));
    }
}
