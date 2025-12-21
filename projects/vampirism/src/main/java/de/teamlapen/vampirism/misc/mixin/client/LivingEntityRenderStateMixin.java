package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.client.renderer.entities.state.IVampirismRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements IVampirismRenderState {

    @Unique
    public Identifier vampirism$convertedOverlay;

    @Unique
    public Identifier vampirism$overlay;

    @Unique
    private int vampirism$blood;

    @Unique
    private boolean vampirism$poisonousBlood;

    @Unique
    private boolean vampirism$isHunter;

    @Unique
    private float vampirism$attackTime;

    @Unique
    private HumanoidArm vampirism$attackArm = HumanoidArm.RIGHT;

    @Override
    public @Nullable Identifier vampirism$overlay() {
        return this.vampirism$overlay;
    }

    @Override
    public void vampirism$overlay(@Nullable Identifier overlay) {
        this.vampirism$overlay = overlay;
    }

    @Override
    public @Nullable Identifier vampirism$convertedOverlay() {
        return vampirism$convertedOverlay;
    }

    @Override
    public void vampirism$convertedOverlay(@Nullable Identifier overlay) {
        vampirism$convertedOverlay = overlay;
    }

    @Override
    public int vampirism$blood() {
        return this.vampirism$blood;
    }

    @Override
    public void vampirism$blood(int blood) {
        this.vampirism$blood = blood;
    }

    @Override
    public boolean vampirism$poisonousBlood() {
        return this.vampirism$poisonousBlood;
    }

    @Override
    public void vampirism$poisonousBlood(boolean poisonous) {
        this.vampirism$poisonousBlood = poisonous;
    }

    @Override
    public boolean vampirism$hunter() {
        return this.vampirism$isHunter;
    }

    @Override
    public void vampirism$hunter(boolean hunter) {
        this.vampirism$isHunter = hunter;
    }


    @Override
    public float vampirism$attackTime() {
        return this.vampirism$attackTime;
    }

    @Override
    public void vampirism$attackTime(float attackTime) {
        this.vampirism$attackTime = attackTime;
    }

    @Override
    public HumanoidArm vampirism$attackArm() {
        return this.vampirism$attackArm;
    }

    @Override
    public void vampirism$attackArm(HumanoidArm arm) {
        this.vampirism$attackArm = arm;
    }

}
