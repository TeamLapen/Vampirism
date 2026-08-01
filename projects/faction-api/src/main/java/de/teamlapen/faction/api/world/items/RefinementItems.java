package de.teamlapen.faction.api.world.items;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record RefinementItems(List<Holder<Item>> amulet, List<Holder<Item>> ring,List<Holder<Item>> belt) {

    public static final StreamCodec<RegistryFriendlyByteBuf, RefinementItems> STREAM_CODEC = StreamCodec.composite(
            Item.STREAM_CODEC.apply(ByteBufCodecs.list()), RefinementItems::amulet,
            Item.STREAM_CODEC.apply(ByteBufCodecs.list()), RefinementItems::ring,
            Item.STREAM_CODEC.apply(ByteBufCodecs.list()), RefinementItems::belt,
            RefinementItems::new
    );
    public static final RefinementItems EMPTY = new RefinementItems(List.of(), List.of(), List.of());

    public RefinementItems(Holder<Item> amulet, Holder<Item> ring, Holder<Item> belt) {
        this(List.of(amulet), List.of(ring), List.of(belt));
    }

    public Stream<IRefinementItem> stream() {
        return Stream.of(amulet, ring, belt).flatMap(Collection::stream).map(Holder::value).map(x -> x instanceof IRefinementItem l ? l : null).filter(Objects::nonNull);
    }

    public Stream<IRefinementItem> stream(IRefinementItem.AccessorySlotType type) {
        var list = switch (type) {
            case AMULET -> amulet;
            case RING -> ring;
            case OBI_BELT -> belt;
        };
        return list.stream().map(Holder::value).map(x -> x instanceof IRefinementItem l ? l : null).filter(Objects::nonNull);
    }
}
