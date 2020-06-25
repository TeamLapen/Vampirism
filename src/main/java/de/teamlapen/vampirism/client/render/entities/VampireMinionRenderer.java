package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.client.render.LayerPlayerBodyOverlay;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends BipedRenderer<VampireMinionEntity, MinionModel<VampireMinionEntity>> {

    private final ResourceLocation[] textures;

    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MinionModel<>(0F), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
        this.addLayer(new LayerPlayerBodyOverlay<>(this, VampireMinionEntity::shouldRenderLordSkin));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));

    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    protected ResourceLocation getEntityTexture(VampireMinionEntity entity) {
        return getVampireTexture(entity.getVampireType());
    }

    @Override
    protected void renderLayers(VampireMinionEntity entity, float p_177093_2_, float p_177093_3_, float p_177093_4_, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
        //Scale layers
        float s = entity.getScale();
        float off = (1 - s) * 1.95f;
        GlStateManager.pushMatrix();
        GlStateManager.scalef(s, s, s);
        GlStateManager.translatef(0.0F, off, 0.0F);
        super.renderLayers(entity, p_177093_2_, p_177093_3_, p_177093_4_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);
        GlStateManager.popMatrix();
    }

    public int getTextureLength() {
        return this.textures.length;
    }
}
