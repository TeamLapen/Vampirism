package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class HunterMinionModel<T extends HunterMinionEntity> extends PlayerModel<T> {

    public HunterMinionModel(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    @Override
    public void setupAnim(T entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.setupAnim(entity, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
        switch (entity.getArmPose()) {
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
                break;
            default:
                break;
        }
    }
}
