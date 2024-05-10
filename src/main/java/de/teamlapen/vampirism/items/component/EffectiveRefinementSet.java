package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IEffectiveRefinementSet;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EffectiveRefinementSet(IRefinementSet set) implements IEffectiveRefinementSet {

    public static final EffectiveRefinementSet EMPTY = new EffectiveRefinementSet(null);
    public static final Codec<EffectiveRefinementSet> CODEC = ModRegistries.REFINEMENT_SETS.byNameCodec().xmap(EffectiveRefinementSet::new, EffectiveRefinementSet::set);
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectiveRefinementSet> STREAM_CODEC = ByteBufCodecs.registry(VampirismRegistries.Keys.REFINEMENT_SET).map(EffectiveRefinementSet::new, EffectiveRefinementSet::set);
}
