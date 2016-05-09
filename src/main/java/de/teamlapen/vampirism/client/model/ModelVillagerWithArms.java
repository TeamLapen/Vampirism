package de.teamlapen.vampirism.client.model;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.util.EnumHandSide;

/**
 * Villager Model with additional arms
 */
public class ModelVillagerWithArms extends ModelVillager {
    public ModelVillagerWithArms(float scale) {
        super(scale);
    }

    public void postRenderArm(float scale, EnumHandSide side) {
        //this.getArmForSide(side).postRender(scale);
    }
}
