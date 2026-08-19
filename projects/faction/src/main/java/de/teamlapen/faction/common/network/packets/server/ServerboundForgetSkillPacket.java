package de.teamlapen.faction.common.network.packets.server;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundForgetSkillPacket(Holder<ISkill<?>> skill, Holder<ISkillTree> skillTree) implements CustomPacketPayload {

    public static final Type<ServerboundForgetSkillPacket> TYPE = new Type<>(FIdentifier.mod("forget_skill"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundForgetSkillPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL), ServerboundForgetSkillPacket::skill,
            ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL_TREE), ServerboundForgetSkillPacket::skillTree,
            ServerboundForgetSkillPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}