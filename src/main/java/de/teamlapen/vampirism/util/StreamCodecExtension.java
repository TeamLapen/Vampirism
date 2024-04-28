package de.teamlapen.vampirism.util;

import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface StreamCodecExtension<B, V> extends StreamCodec<B, V> {

    static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1, final Function<C, T1> func1,
            final StreamCodec<? super B, T2> codec2, final Function<C, T2> func2,
            final StreamCodec<? super B, T3> codec3, final Function<C, T3> func3,
            final StreamCodec<? super B, T4> codec4, final Function<C, T4> func4,
            final StreamCodec<? super B, T5> codec5, final Function<C, T5> func5,
            final StreamCodec<? super B, T6> codec6, final Function<C, T6> func6,
            final StreamCodec<? super B, T7> codec7, final Function<C, T7> func7,

            final Function7<T1, T2, T3, T4, T5, T6, T7, C> result
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                return result.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull C obj) {
                codec1.encode(buffer, func1.apply(obj));
                codec2.encode(buffer, func2.apply(obj));
                codec3.encode(buffer, func3.apply(obj));
                codec4.encode(buffer, func4.apply(obj));
                codec5.encode(buffer, func5.apply(obj));
                codec6.encode(buffer, func6.apply(obj));
                codec7.encode(buffer, func7.apply(obj));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1, final Function<C, T1> func1,
            final StreamCodec<? super B, T2> codec2, final Function<C, T2> func2,
            final StreamCodec<? super B, T3> codec3, final Function<C, T3> func3,
            final StreamCodec<? super B, T4> codec4, final Function<C, T4> func4,
            final StreamCodec<? super B, T5> codec5, final Function<C, T5> func5,
            final StreamCodec<? super B, T6> codec6, final Function<C, T6> func6,
            final StreamCodec<? super B, T7> codec7, final Function<C, T7> func7,
            final StreamCodec<? super B, T8> codec8, final Function<C, T8> func8,

            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> result
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                return result.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull C obj) {
                codec1.encode(buffer, func1.apply(obj));
                codec2.encode(buffer, func2.apply(obj));
                codec3.encode(buffer, func3.apply(obj));
                codec4.encode(buffer, func4.apply(obj));
                codec5.encode(buffer, func5.apply(obj));
                codec6.encode(buffer, func6.apply(obj));
                codec7.encode(buffer, func7.apply(obj));
                codec8.encode(buffer, func8.apply(obj));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> codec1, final Function<C, T1> func1,
            final StreamCodec<? super B, T2> codec2, final Function<C, T2> func2,
            final StreamCodec<? super B, T3> codec3, final Function<C, T3> func3,
            final StreamCodec<? super B, T4> codec4, final Function<C, T4> func4,
            final StreamCodec<? super B, T5> codec5, final Function<C, T5> func5,
            final StreamCodec<? super B, T6> codec6, final Function<C, T6> func6,
            final StreamCodec<? super B, T7> codec7, final Function<C, T7> func7,
            final StreamCodec<? super B, T8> codec8, final Function<C, T8> func8,
            final StreamCodec<? super B, T9> codec9, final Function<C, T9> func9,

            final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> result
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                T9 t9 = codec9.decode(buffer);
                return result.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull C obj) {
                codec1.encode(buffer, func1.apply(obj));
                codec2.encode(buffer, func2.apply(obj));
                codec3.encode(buffer, func3.apply(obj));
                codec4.encode(buffer, func4.apply(obj));
                codec5.encode(buffer, func5.apply(obj));
                codec6.encode(buffer, func6.apply(obj));
                codec7.encode(buffer, func7.apply(obj));
                codec8.encode(buffer, func8.apply(obj));
                codec9.encode(buffer, func9.apply(obj));
            }
        };
    }




}
