package de.teamlapen.vampirism.common.world.entity.dracula.ai;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum DraculaState implements StringRepresentable {
    DEFAULT("default", FightStage.PHASE_1),
    PASSIVE("passive", FightStage.PHASE_1),
    TRANSFORMING_TO_RANGED("transform_to_ranged", FightStage.PHASE_2,true, 5*20),
    RANGED("ranged", FightStage.PHASE_2),
    TRANSFORMING_TO_RAGED("transforming_to_raged",FightStage.PHASE_3, true, 5*20),
    RAGED("raged",FightStage.PHASE_3),
    MIST("mist", FightStage.PHASE_3)
    ;

    public static final StreamCodec<? super RegistryFriendlyByteBuf, DraculaState> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(DraculaState.class);
    public static final Codec<DraculaState> CODEC = StringRepresentable.fromEnum(DraculaState::values);

    public final String name;
    public final FightStage stage;
    public final boolean isTransforming;
    public final int transformTime;

    DraculaState(String name, FightStage stage, boolean isTransforming, int transformTime) {
        this.name = name;
        this.stage = stage;
        this.isTransforming = isTransforming;
        this.transformTime = transformTime;
    }

    DraculaState(String name, FightStage stage) {
        this(name, stage, false, 0);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
