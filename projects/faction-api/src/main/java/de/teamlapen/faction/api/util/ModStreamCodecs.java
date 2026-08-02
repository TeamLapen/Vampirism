package de.teamlapen.faction.api.util;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3d;

import java.util.Set;

public class ModStreamCodecs {
    public static final StreamCodec<ByteBuf, Vector3d> VECTOR3D = new StreamCodec<>() {
        @Override
        public Vector3d decode(ByteBuf p_320376_) {
            return new Vector3d(p_320376_.readDouble(), p_320376_.readDouble(), p_320376_.readDouble());
        }

        @Override
        public void encode(ByteBuf p_320158_, Vector3d p_320396_) {
            p_320158_.writeDouble(p_320396_.x);
            p_320158_.writeDouble(p_320396_.y);
            p_320158_.writeDouble(p_320396_.z);
        }
    };

    public static <B, T, Z> StreamCodec<B, Pair<T, Z>> pair(StreamCodec<? super B, T> stream1, StreamCodec<? super B, Z> stream2) {
        return new StreamCodec<>() {
            @Override
            public Pair<T, Z> decode(B buffer) {
                return Pair.of(stream1.decode(buffer), stream2.decode(buffer));
            }

            @Override
            public void encode(B buffer, Pair<T, Z> pair) {
                stream1.encode(buffer, pair.getFirst());
                stream2.encode(buffer, pair.getSecond());
            }
        };
    }

    public static <B extends RegistryFriendlyByteBuf, R, C, V> StreamCodec<B, Table<R,C,V>> table(StreamCodec<? super RegistryFriendlyByteBuf, R> rowCodec, StreamCodec<? super RegistryFriendlyByteBuf, C> columnCodec, StreamCodec<? super RegistryFriendlyByteBuf, V> valueCodec) {
        return new StreamCodec<>() {
            @Override
            public Table<R, C, V> decode(B buffer) {
                int size = ByteBufCodecs.VAR_INT.decode(buffer);
                ImmutableTable.Builder<R, C, V> table = ImmutableTable.builder();

                for (int i = 0; i < size; i++) {
                    R row = rowCodec.decode(buffer);
                    C column = columnCodec.decode(buffer);
                    V value = valueCodec.decode(buffer);
                    table.put(row, column, value);
                }

                return table.build();
            }

            @Override
            public void encode(B buffer, Table<R, C, V> rcvTable) {
                Set<Table.Cell<R, C, V>> cells = rcvTable.cellSet();
                ByteBufCodecs.VAR_INT.encode(buffer, cells.size());

                for (Table.Cell<R, C, V> cell : cells) {
                    rowCodec.encode(buffer, cell.getRowKey());
                    columnCodec.encode(buffer, cell.getColumnKey());
                    valueCodec.encode(buffer, cell.getValue());
                }
            }
        };
    }
}
