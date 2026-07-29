package de.teamlapen.faction.api.factions.village;

import de.teamlapen.faction.api.world.ITotem;
import de.teamlapen.faction.api.world.blocks.FactionBlockAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

public record TotemPair(Holder<Block> fragile, Holder<Block> crafted) {
    public static final TotemPair DEFAULT = new TotemPair(FactionBlockAccess.TOTEM_TOP, FactionBlockAccess.TOTEM_TOP_CRAFTED);
    public static final StreamCodec<RegistryFriendlyByteBuf, TotemPair> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.BLOCK), TotemPair::fragile,
            ByteBufCodecs.holderRegistry(Registries.BLOCK), TotemPair::crafted,
            TotemPair::new
    );

    public Holder<Block> get(boolean crafted) {
        return crafted ? this.crafted : this.fragile;
    }
}
