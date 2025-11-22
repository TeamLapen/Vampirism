package de.teamlapen.factions.common.network.packets.server;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillTree;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;


public record ServerboundUnlockSkillPacket(Holder<ISkill<?>> skill, Holder<ISkillTree> skillTree) implements CustomPacketPayload {
    public static final Type<ServerboundUnlockSkillPacket> TYPE = new Type<>(FResourceLocation.mod("unlock_skill"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundUnlockSkillPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL), ServerboundUnlockSkillPacket::skill,
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL_TREE), ServerboundUnlockSkillPacket::skillTree,
            ServerboundUnlockSkillPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
