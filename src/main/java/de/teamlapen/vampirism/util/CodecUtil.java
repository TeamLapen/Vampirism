package de.teamlapen.vampirism.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CodecUtil {
    public static final Codec<UUID> UUID = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
    public static final Codec<ChunkPos> CHUNK_POS = Codec.INT_STREAM.comapFlatMap(p -> Util.fixedSize(p, 2).map(l -> new ChunkPos(l[0], l[1])), p -> IntStream.of(p.x, p.z));
    public static final PrimitiveCodec<DoubleStream> DOUBLE_STREAM = new PrimitiveCodec<DoubleStream>() {
        @Override
        public <T> DataResult<DoubleStream> read(final DynamicOps<T> ops, final T input) {
            return ops.getStream(input).flatMap(s -> {
                final List<T> list = s.collect(Collectors.toList());
                if(list.stream().allMatch(element -> ops.getNumberValue(element).result().isPresent())) {
                    return DataResult.success(list.stream().mapToDouble(element -> ops.getNumberValue(element).result().get().doubleValue()));
                }
                return DataResult.error(() -> "Some elements are not doubles: " + input);
            });
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final DoubleStream value) {
            return ops.createList(value.mapToObj(ops::createNumeric));
        }

        @Override
        public String toString() {
            return "DoubleStream";
        }
    };
    public static final Codec<AABB> AABB = DOUBLE_STREAM.comapFlatMap(p -> fixedSize(p, 6).map(l -> new AABB(l[0], l[1], l[2], l[3], l[4], l[5])), p -> DoubleStream.of(p.minX, p.minY, p.minZ, p.maxX, p.maxY, p.maxZ));

    public static <T> Codec<Object2IntMap<T>> objectToIntMap(Codec<T> keyCodec) {
        return Codec.unboundedMap(keyCodec, Codec.INT).xmap(Object2IntOpenHashMap::new, Object2IntMaps::unmodifiable);
    }

    public static DataResult<double[]> fixedSize(DoubleStream pStream, int pSize) {
        double[] aint = pStream.limit((long)(pSize + 1)).toArray();
        if (aint.length != pSize) {
            Supplier<String> supplier = () -> "Input is not a list of " + pSize + " ints";
            return aint.length >= pSize ? DataResult.error(supplier, Arrays.copyOf(aint, pSize)) : DataResult.error(supplier);
        } else {
            return DataResult.success(aint);
        }
    }

}
