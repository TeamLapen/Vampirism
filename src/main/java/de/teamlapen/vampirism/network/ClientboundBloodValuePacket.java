package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ClientboundBloodValuePacket(Map<ResourceLocation, Float>[] values, Map<EntityType<?>, ResourceLocation> convertibleOverlay) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "blood_value");
    public static final Codec<ClientboundBloodValuePacket> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).listOf().fieldOf("values").forGetter(l -> List.of(l.values)),
                Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), ResourceLocation.CODEC).fieldOf("convertible_overlay").forGetter(l -> l.convertibleOverlay)
        ).apply(builder, (values1, overlay) -> new ClientboundBloodValuePacket(values1.toArray((Map<ResourceLocation, Float>[]) new Map[0]), overlay));
    });

    public Map<ResourceLocation, Float>[] getValues() {
        return values;
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
