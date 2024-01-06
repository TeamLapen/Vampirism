package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ServerboundRequestSkillTreePacket() implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "request_skill_tree");

    public ServerboundRequestSkillTreePacket(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {

    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
