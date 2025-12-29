package de.teamlapen.factions.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.world.items.components.IFactionSlayer;
import de.teamlapen.factions.common.core.ModRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;

public record FactionSlayer(HolderSet<IFaction<?>> slayedFactions, float multiplier) implements IFactionSlayer {

    public static final Codec<FactionSlayer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION).fieldOf("slayedFaction").forGetter(FactionSlayer::slayedFactions),
            Codec.FLOAT.fieldOf("multiplier").forGetter(FactionSlayer::multiplier)
    ).apply(inst, FactionSlayer::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionSlayer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(FactionRegistries.Keys.FACTION), FactionSlayer::slayedFactions,
            ByteBufCodecs.FLOAT, FactionSlayer::multiplier,
            FactionSlayer::new
    );

    public static FactionSlayer create(TagKey<IFaction<?>> tag, float multiplier) {
        HolderGetter<IFaction<?>> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.FACTIONS);
        return new FactionSlayer(holderGetter.getOrThrow(tag), multiplier);
    }
}
