package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterModel<T extends LivingEntity> extends BipedCloakedModel<T> {

    public BasicHunterModel( boolean smallArms) {
        super(0.0F, smallArms);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof IVampirismCrossbowUser) {
            switch (((IVampirismCrossbowUser) entityIn).getArmPose()) {
                case CROSSBOW_HOLD:
                    ModelHelper.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                    break;
                case CROSSBOW_CHARGE:
                    ModelHelper.animateCrossbowCharge(this.rightArm, this.leftArm, entityIn, true);
                    break;
                default:
                    break;
            }
        }
    }
}
