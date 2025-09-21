package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundRecipesPacket(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {

    public static final Type<ClientboundRecipesPacket> TYPE = new Type<>(VResourceLocation.mod("recipes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRecipesPacket> CODEC = StreamCodec.composite(
            RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), (ClientboundRecipesPacket s) -> s.recipes, ClientboundRecipesPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
