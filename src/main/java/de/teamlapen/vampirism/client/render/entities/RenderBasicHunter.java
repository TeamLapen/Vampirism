package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class RenderBasicHunter extends RenderBiped<EntityBasicHunter> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base1.png");
    private final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_base5.png")
    };
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire_hunter_extra.png");

    public RenderBasicHunter(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBasicHunter(), 0.5F);
    }

    @Override
    public void doRender(EntityBasicHunter entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBasicHunter entity) {
        int level = entity.getLevel();
        if (level > 0) return texture;
        return textures[entity.getEntityId() % textures.length];
    }

    @Override
    protected void renderModel(EntityBasicHunter entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float partTicks) {
        int level = entitylivingbaseIn.getLevel();
        int type = entitylivingbaseIn.getEntityId() % textures.length;
        if (level == 0) {
            ((ModelBasicHunter) mainModel).setSkipCloakOnce();
        }
        super.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, partTicks);
        bindTexture(textureExtra);
        ((ModelBasicHunter) mainModel).renderHat(partTicks, level == 0 ? type : -1);
        ((ModelBasicHunter) mainModel).renderWeapons(partTicks, level < 2 || entitylivingbaseIn.isCrossbowInMainhand());

    }
}
