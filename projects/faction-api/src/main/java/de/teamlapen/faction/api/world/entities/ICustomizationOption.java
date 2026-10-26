package de.teamlapen.faction.api.world.entities;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface ICustomizationOption<T> {

    Identifier id();

    StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
}
