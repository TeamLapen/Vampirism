package de.teamlapen.vampirism.client.renderer.entities.state;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.HumanoidArm;

public interface IVampirismRenderState {
    ContextKey<Integer> BLOOD = new ContextKey<>(VResourceLocation.mod("blood"));
    ContextKey<Integer> MAX_BLOOD = new ContextKey<>(VResourceLocation.mod("max_blood"));
    ContextKey<Boolean> POISON_BLOOD = new ContextKey<>(VResourceLocation.mod("poisonous_blood"));
    ContextKey<Float> ATTACK_TIME = new ContextKey<>(VResourceLocation.mod("attack_time"));
    ContextKey<HumanoidArm> ATTACK_ARM = new ContextKey<>(VResourceLocation.mod("attack_arm"));
    ContextKey<Identifier> OVERLAY = new ContextKey<>(VResourceLocation.mod("overlay"));
    ContextKey<Identifier> CONVERTED_OVERLAY = new ContextKey<>(VResourceLocation.mod("converted_overlay"));
    ContextKey<Boolean> HUNTER = new ContextKey<>(VResourceLocation.mod("hunter"));
}
