package de.teamlapen.vampirism.common.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.common.core.ModRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;

public record FactionSlayer(HolderSet<IFaction<?>> slayedFactions, float multiplier) {

    public static final Codec<FactionSlayer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(VampirismRegistries.Keys.FACTION).fieldOf("slayedFaction").forGetter(FactionSlayer::slayedFactions),
            Codec.FLOAT.fieldOf("multiplier").forGetter(FactionSlayer::multiplier)
    ).apply(inst, FactionSlayer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionSlayer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(VampirismRegistries.Keys.FACTION), FactionSlayer::slayedFactions,
            ByteBufCodecs.FLOAT, FactionSlayer::multiplier,
            FactionSlayer::new
    );

    public static FactionSlayer create(TagKey<IFaction<?>> tag, float multiplier) {
        HolderGetter<IFaction<?>> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.FACTIONS);
        return new FactionSlayer(holderGetter.getOrThrow(tag), multiplier);
    }
}
