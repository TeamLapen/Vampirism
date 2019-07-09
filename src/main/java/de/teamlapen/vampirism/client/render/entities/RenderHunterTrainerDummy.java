package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainerDummy;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHunterTrainerDummy extends BipedRenderer<EntityHunterTrainerDummy> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base1.png");

    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_extra.png");

    public RenderHunterTrainerDummy(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelBasicHunter(), 0.5F);
    }

    @Override
    public void doRender(EntityHunterTrainerDummy entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHunterTrainerDummy entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(EntityHunterTrainerDummy entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }

    @Override
    protected void renderModel(EntityHunterTrainerDummy entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float partTicks) {
        super.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, partTicks);
        bindTexture(textureExtra);
    }
}
