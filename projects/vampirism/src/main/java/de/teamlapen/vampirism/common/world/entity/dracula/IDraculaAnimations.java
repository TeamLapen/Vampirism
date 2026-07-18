package de.teamlapen.vampirism.common.world.entity.dracula;


import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IDraculaAnimations {

//    RawAnimation PHASE_2_TRANSFORMATION = AnimationDefinition.Builder.withLength().begin().thenPlay("transformation.phase2");
//    RawAnimation PHASE_3_TRANSFORMATION = RawAnimation.begin().thenPlay("transformation.phase3");

//    RawAnimation PHASE_3_ATTACK_1 = RawAnimation.begin().thenPlay("attack.melee.1");
//    RawAnimation PHASE_3_ATTACK_2 = RawAnimation.begin().thenPlay("attack.melee.2");

    enum Animation {

        NONE(VIdentifier.mod("none")),
        NEEDLE_1(VIdentifier.mod("needle_1")),
        NEEDLE_2(VIdentifier.mod("needle_2")),
        FLYING_SWORD_1(VIdentifier.mod("flying_sword_1")),
        FLYING_SWORD_2(VIdentifier.mod("flying_sword_2")),
        ATTACK_1(VIdentifier.mod("attack_1")),
        ATTACK_2(VIdentifier.mod("attack_2")),
        BLOOD_SIPHON(VIdentifier.mod("blood_siphon"))
        ;

        public static final StreamCodec<? super RegistryFriendlyByteBuf, Animation> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Animation.class);
        public static final Map<Identifier, Animation> BY_ID = Arrays.stream(values()).collect(Collectors.toMap(Animation::animationId, Function.identity()));

        private final Identifier animationId;

        Animation(Identifier animationId) {
            this.animationId = animationId;
        }

        public Identifier animationId() {
            return animationId;
        }
    }

}
