package de.teamlapen.faction.common.components;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.components.IEffectiveRefinementSet;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record EffectiveRefinementSet(@Nullable Holder<IRefinementSet> set) implements IEffectiveRefinementSet {

    public static final EffectiveRefinementSet EMPTY = new EffectiveRefinementSet(null);
    public static final Codec<EffectiveRefinementSet> CODEC = ExtraCodecs.optionalEmptyMap(RegistryFixedCodec.create(FactionRegistries.Keys.REFINEMENT_SET)).xmap(x -> new EffectiveRefinementSet(x.orElse(null)), x -> Optional.ofNullable(x.set()));
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectiveRefinementSet> STREAM_CODEC = ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.REFINEMENT_SET)).map(x -> new EffectiveRefinementSet(x.orElse(null)), x -> Optional.ofNullable(x.set()));
}
