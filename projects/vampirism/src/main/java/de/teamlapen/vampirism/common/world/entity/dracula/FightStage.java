package de.teamlapen.vampirism.common.world.entity.dracula;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Locale;

public enum FightStage implements StringRepresentable {
    NONE,
    PHASE_1,
    PHASE_2,
    PHASE_3;

    public static Codec<FightStage> CODEC = StringRepresentable.fromEnum(FightStage::values);
    public static StreamCodec<FriendlyByteBuf, FightStage> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(FightStage.class);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}