package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ServerboundActionBindingPacket(int actionBindingId, @Nullable IAction<?> action) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "action_binding");
    public static final Codec<ServerboundActionBindingPacket> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("action_binding_id").forGetter(ServerboundActionBindingPacket::actionBindingId),
            ModRegistries.ACTIONS.byNameCodec().optionalFieldOf("action", null).forGetter(ServerboundActionBindingPacket::action)
    ).apply(inst, ServerboundActionBindingPacket::new));


    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
