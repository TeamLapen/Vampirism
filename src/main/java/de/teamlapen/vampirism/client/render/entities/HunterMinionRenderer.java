package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends BipedRenderer<HunterMinionEntity, BasicHunterModel<HunterMinionEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");
    private final ResourceLocation[] textures = {
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base2.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base3.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base4.png"),
            new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base5.png")
    };
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");

    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BasicHunterModel<>(), 0.5F);
        //this.addLayer(new LayerPlayerBodyOverlay<>(this));
    }


//    @Override
//    protected ResourceLocation getEntityTexture(HunterMinion entity) {
//        int level = entity.getLevel();
//        if (level > 0) return texture;
//        return textures[entity.getEntityTextureType() % textures.length];
//    }
//
//    @Override
//    protected void renderModel(HunterMinion entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float partTicks) {
//        int level = entitylivingbaseIn.getLevel();
//        int type = entitylivingbaseIn.getEntityTextureType() % textures.length;
//        if (level == 0) {
//            getEntityModel().setSkipCloakOnce();
//        }
//        super.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, partTicks);
//        bindTexture(textureExtra);
//        getEntityModel().renderHat(partTicks, level == 0 ? type : -1);
//        getEntityModel().renderWeapons(partTicks, level < 2 || entitylivingbaseIn.isCrossbowInMainhand());
//
//    }
}