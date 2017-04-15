package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.render.entities.RenderAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Render the eyes over the advanced vampire custom face
 */
@SideOnly(Side.CLIENT)
public class LayerAdvancedVampireEye implements LayerRenderer<EntityAdvancedVampire> {

    private final RenderAdvancedVampire renderer;

    private final ResourceLocation[] overlays;

    public LayerAdvancedVampireEye(RenderAdvancedVampire renderer) {
        this.renderer = renderer;
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/eyes" + (i) + ".png");
        }
    }

    @Override
    public void doRenderLayer(EntityAdvancedVampire entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        int type = entitylivingbaseIn.getEyeType();
        if (type < 0 || type >= overlays.length) {
            type = 0;
        }
        this.renderer.bindTexture(overlays[type]);

        GlStateManager.pushMatrix();
        if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        ((ModelBiped) this.renderer.getMainModel()).bipedHead.render(scale);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
