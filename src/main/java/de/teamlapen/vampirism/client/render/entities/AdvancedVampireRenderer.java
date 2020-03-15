package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.render.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireRenderer extends BipedRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/advanced_vampire.png");

    public AdvancedVampireRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedModel<>(RenderType::entityCutoutNoCull, 0F, 0F, 64, 64), 0.5F);
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.addLayer(new AdvancedVampireEyeLayer(this));

        }
    }

    @Override
    public ResourceLocation getEntityTexture(AdvancedVampireEntity entity) {
        return texture;
    }


    @Override
    protected void renderName(AdvancedVampireEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
        double dist = this.renderManager.squareDistanceTo(p_225629_1_);
        if (dist <= 256) {
            super.renderName(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
        }
    }
}
