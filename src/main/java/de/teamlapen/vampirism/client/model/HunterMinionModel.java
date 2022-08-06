package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelHelper;

public class HunterMinionModel<T extends HunterMinionEntity> extends PlayerModel<T> {

    public HunterMinionModel(float p_i46304_1_, boolean p_i46304_2_) {
        super(p_i46304_1_, p_i46304_2_);
    }

    @Override
    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
        switch (p_225597_1_.getArmPose()) {
            case CROSSBOW_HOLD:
                ModelHelper.animateCrossbowHold(this.rightSleeve, this.leftSleeve, this.head, true);
                break;
            case CROSSBOW_CHARGE:
                ModelHelper.animateCrossbowCharge(this.rightSleeve, this.leftSleeve, p_225597_1_, true);
                break;
            default:
                break;
        }
    }
}
