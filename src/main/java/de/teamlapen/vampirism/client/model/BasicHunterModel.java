package de.teamlapen.vampirism.client.model;

import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Model for Basic Vampire Hunter
 */
@OnlyIn(Dist.CLIENT)
public class BasicHunterModel<T extends LivingEntity> extends BipedCloakedModel<T> {
    private boolean targetingLeft = false;
    private boolean targetingRight = false;
    private float xAngle = 0;

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
    public void prepareMobModel(@NotNull T entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
        this.targetingRight = false;
        this.targetingLeft = false;
        ItemStack itemStack = entitylivingbaseIn.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof VampirismItemCrossbow && entitylivingbaseIn instanceof BasicHunterEntity && ((BasicHunterEntity) entitylivingbaseIn).isSwingingArms()) {
            if (entitylivingbaseIn.getMainArm() == HumanoidArm.RIGHT) {
                this.targetingRight = true;
            } else {
                this.targetingLeft = true;
            }
            xAngle = -((BasicHunterEntity) entitylivingbaseIn).getTargetAngle() - (float) Math.PI / 3;
        }

        super.prepareMobModel(entitylivingbaseIn, p_78086_2_, p_78086_3_, partialTickTime);

    }

    @Override
    public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (targetingRight) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = xAngle;
            this.leftArm.xRot = xAngle / 2F;
        } else if (targetingLeft) {
            this.leftArm.yRot = 0.1F + this.head.yRot;
            this.rightArm.xRot = xAngle / 2F;
            this.leftArm.xRot = xAngle;
        }
    }
}
