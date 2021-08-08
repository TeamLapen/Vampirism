package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelBiped with a cloak
 */
@OnlyIn(Dist.CLIENT)
public class BipedCloakedModel<T extends LivingEntity> extends PlayerModel<T> {
    protected ModelPart bipedCloak;

    public BipedCloakedModel(float modelSize, boolean smallArms) {
        super(modelSize, smallArms);
        bipedCloak = new ModelPart(this, 0, 0);
        bipedCloak.setTexSize(64, 32);
        bipedCloak.addBox(-7.0F, 0.0F, 0.4F, 14, 20, 1);
        bipedCloak.setPos(0, 0, 2);
    }

    public void renderCloak(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn) {
        this.bipedCloak.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);
        bipedCloak.visible = visible;
    }

    @Override
    public void setupAnim(T entity, float f, float f1, float f2, float f3, float f4) {
        super.setupAnim(entity, f, f1, f2, f3, f4);
        if (entity.isCrouching()) {
            this.bipedCloak.y = 2.0F;
        } else {
            this.bipedCloak.y = 0.0F;
        }
    }
}
