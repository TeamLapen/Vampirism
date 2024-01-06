package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;


public record ServerboundDeleteRefinementPacket(IRefinementItem.AccessorySlotType slot) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "delete_refinement");
    public static final Codec<ServerboundDeleteRefinementPacket> CODEC = StringRepresentable.fromEnum(IRefinementItem.AccessorySlotType::values).xmap(ServerboundDeleteRefinementPacket::new, msg -> msg.slot);

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
