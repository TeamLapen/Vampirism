package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.entity.hunter.HunterTaskMasterEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class HunterTaskMasterRenderer extends BipedRenderer<HunterTaskMasterEntity, BasicHunterModel<HunterTaskMasterEntity>> {
    private final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base5.png")
    };
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");

    public HunterTaskMasterRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BasicHunterModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getEntityTexture(@Nonnull HunterTaskMasterEntity entity) {
        return textures[entity.getEntityId() % textures.length];
    }

    @Override
    protected void renderName(HunterTaskMasterEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        double dist = this.renderManager.squareDistanceTo(entityIn);
        if (dist <= 256) {
            super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }


}