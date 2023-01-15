package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterModel<T extends LivingEntity> extends BipedCloakedModel<T> {

    public static @NotNull LayerDefinition createBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(false), 64, 64);
    }

    public static @NotNull LayerDefinition createSlimBodyLayer() {
        return LayerDefinition.create(BipedCloakedModel.createMesh(true), 64, 64);
    }

    public BasicHunterModel(ModelPart part, boolean smallArms) {
        super(part, smallArms);
    }


    @Override
    public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityIn instanceof IVampirismCrossbowUser) {
            switch (((IVampirismCrossbowUser) entityIn).getArmPose()) {
                case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entityIn, true);
            }
        }
    }
}
