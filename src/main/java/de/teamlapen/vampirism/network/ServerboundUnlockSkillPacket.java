package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;


public record ServerboundUnlockSkillPacket(Holder<ISkill<?>> skill, Holder<ISkillTree> skillTree) implements CustomPacketPayload {
    public static final Type<ServerboundUnlockSkillPacket> TYPE = new Type<>(VResourceLocation.mod("unlock_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUnlockSkillPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.SKILL), ServerboundUnlockSkillPacket::skill,
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.SKILL_TREE), ServerboundUnlockSkillPacket::skillTree,
            ServerboundUnlockSkillPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
