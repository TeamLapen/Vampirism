package de.teamlapen.vampirism.client.renderer.entities.state;

import net.minecraft.world.entity.HumanoidArm;

public interface IEntityRenderState {

    boolean vampirism$hunter();

    void vampirism$hunter(boolean hunter);

    float vampirism$attackTime();

    void vampirism$attackTime(float attackTime);

    HumanoidArm vampirism$attackArm();

    void vampirism$attackArm(HumanoidArm arm);

    boolean sleeping$inCoffin();

    void sleeping$inCoffin(boolean sleepingInCoffin);
}
