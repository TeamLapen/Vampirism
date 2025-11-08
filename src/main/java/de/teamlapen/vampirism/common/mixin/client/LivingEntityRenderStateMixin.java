package de.teamlapen.vampirism.common.mixin.client;

import de.teamlapen.vampirism.client.renderer.entities.state.IVampirismRenderState;
import de.teamlapen.vampirism.common.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements IVampirismRenderState {

    @Unique
    public ResourceLocation vampirism$convertedOverlay;

    @Unique
    public ResourceLocation vampirism$overlay;

    @Unique
    private int vampirism$blood;

    @Unique
    private boolean vampirism$poisonousBlood;

    @Unique
    private boolean vampirism$isHunter;

    @Unique
    private boolean vampirism$sleepingInCoffin;

    @Unique
    private float vampirism$attackTime;

    @Unique
    private HumanoidArm vampirism$attackArm = HumanoidArm.RIGHT;

    @Unique
    private Bat vampirism$bat;

    @Unique
    private VampirismPlayerAttributes vampirism$vampirismAttributes = new VampirismPlayerAttributes();

    @Override
    public @Nullable ResourceLocation vampirism$overlay() {
        return this.vampirism$overlay;
    }

    @Override
    public void vampirism$overlay(@Nullable ResourceLocation overlay) {
        this.vampirism$overlay = overlay;
    }

    @Override
    public @Nullable ResourceLocation vampirism$convertedOverlay() {
        return vampirism$convertedOverlay;
    }

    @Override
    public void vampirism$convertedOverlay(@Nullable ResourceLocation overlay) {
        vampirism$convertedOverlay = overlay;
    }

    @Override
    public VampirismPlayerAttributes vampirism$attributes() {
        return vampirism$vampirismAttributes;
    }

    @Override
    public void vampirism$attributes(VampirismPlayerAttributes attributes) {
        this.vampirism$vampirismAttributes = attributes;
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
    public boolean sleeping$inCoffin() {
        return this.vampirism$sleepingInCoffin;
    }

    @Override
    public void sleeping$inCoffin(boolean sleepingInCoffin) {
        this.vampirism$sleepingInCoffin = sleepingInCoffin;
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

    @Override
    public Bat vampirism$bat() {
        return this.vampirism$bat;
    }

    @Override
    public void vampirism$bat(Bat bat) {
        this.vampirism$bat = bat;
    }
}
