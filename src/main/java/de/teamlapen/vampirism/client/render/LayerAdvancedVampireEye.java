package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the eyes over the advanced vampire custom face
 */
@OnlyIn(Dist.CLIENT)
public class LayerAdvancedVampireEye extends LayerRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> {

    private final IEntityRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> renderer;

    private final ResourceLocation[] overlays;

    public LayerAdvancedVampireEye(IEntityRenderer<AdvancedVampireEntity, BipedModel<AdvancedVampireEntity>> renderer) {
        super(renderer);
        this.renderer = renderer;
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/eyes" + (i) + ".png");
        }
    }

    @Override
    public void render(AdvancedVampireEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        int type = entitylivingbaseIn.getEyeType();
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }
        this.renderer.bindTexture(overlays[type]);

        RenderSystem.pushMatrix();
        if (entitylivingbaseIn.func_225608_bj_()) {//isSneaking
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
        }
        this.renderer.getEntityModel().bipedHead.render(scale);
        RenderSystem.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
