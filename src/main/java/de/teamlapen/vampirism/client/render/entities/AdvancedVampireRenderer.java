package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireRenderer extends HumanoidMobRenderer<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/advanced_vampire.png");

    public AdvancedVampireRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn, new HumanoidModel<>(RenderType::entityCutoutNoCull, 0F, 0F, 64, 64), 0.5F);
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.addLayer(new AdvancedVampireEyeLayer(this));

        }
    }

    @Override
    public ResourceLocation getTextureLocation(AdvancedVampireEntity entity) {
        return texture;
    }


    @Override
    protected void renderNameTag(AdvancedVampireEntity entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 256) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
