package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record SelectedAmmunition(Item item) {

    public static final SelectedAmmunition EMPTY = new SelectedAmmunition(null);

    public static final Codec<SelectedAmmunition> CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(SelectedAmmunition::new, SelectedAmmunition::item);
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectedAmmunition> STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM).map(SelectedAmmunition::new, SelectedAmmunition::item);

}
