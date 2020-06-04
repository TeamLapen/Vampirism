package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.entity.vampire.VampireTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class VampireTaskMasterRenderer extends BipedRenderer<VampireTaskMasterEntity, BipedModel<VampireTaskMasterEntity>> {
    private final ResourceLocation[] textures;

    public VampireTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0F,false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().getAllResourceLocations("textures/entity/vampire", s -> s.endsWith(".png")).stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(ResourceLocation[]::new);
    }

    public ResourceLocation getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    public ResourceLocation getEntityTexture(VampireTaskMasterEntity entity) {
        return getVampireTexture(entity.getEntityId());
    }

    @Override
    protected void renderName(VampireTaskMasterEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double dist = this.renderManager.squareDistanceTo(entityIn);
        if (dist <= 256) {
            super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }


}
