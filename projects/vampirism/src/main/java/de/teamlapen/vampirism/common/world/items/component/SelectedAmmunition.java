package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.world.items.components.ISelectedAmmunition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SelectedAmmunition(@Nullable Item item) implements ISelectedAmmunition {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private SelectedAmmunition(Optional<Item> item) {
        this(item.orElse(null));
    }
    public static final SelectedAmmunition EMPTY = new SelectedAmmunition((Item) null);

    public static final Codec<SelectedAmmunition> CODEC = BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("ammo").codec().xmap(SelectedAmmunition::new, x -> Optional.ofNullable(x.item()));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectedAmmunition> STREAM_CODEC = ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)).map(SelectedAmmunition::new, x -> Optional.ofNullable(x.item()));

}
