package de.teamlapen.vampirism.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * ModelBiped with a cloak
 */
@OnlyIn(Dist.CLIENT)
public class BipedCloakedModel<T extends LivingEntity> extends BipedModel<T> {
    protected ModelRenderer bipedCloak;

    public BipedCloakedModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
        super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
        bipedCloak = new ModelRenderer(this, 0, 0);
        bipedCloak.setTextureSize(64, 32);
        bipedCloak.addBox(-7.0F, 0.0F, 0.4F, 14, 20, 1);
        bipedCloak.setRotationPoint(0, 0, 2);
    }

    public void renderCloak(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn) {
        this.bipedCloak.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        bipedCloak.showModel = visible;
    }

    @Override
    public void setRotationAngles(T entity, float f, float f1, float f2, float f3, float f4) {
        super.setRotationAngles(entity, f, f1, f2, f3, f4);
        if (entity.isCrouching()) {
            this.bipedCloak.rotationPointY = 2.0F;
        } else {
            this.bipedCloak.rotationPointY = 0.0F;
        }
    }
}
