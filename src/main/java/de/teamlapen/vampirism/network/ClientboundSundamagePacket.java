package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundSundamagePacket(List<ResourceKey<DimensionType>> dimensions, List<ResourceKey<Biome>> biomes, List<ResourceKey<Level>> noSunDamageLevels, List<ResourceKey<Level>> sunDamageLevels) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "sundamage");
    public static final Codec<ClientboundSundamagePacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ResourceKey.codec(Registries.DIMENSION_TYPE).listOf().fieldOf("dimensions").forGetter(ClientboundSundamagePacket::dimensions),
                    ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biomes").forGetter(ClientboundSundamagePacket::biomes),
                    ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("noSunDamageLevels").forGetter(ClientboundSundamagePacket::noSunDamageLevels),
                    ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("sunDamageLevels").forGetter(ClientboundSundamagePacket::sunDamageLevels)
            ).apply(inst, ClientboundSundamagePacket::new));

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
