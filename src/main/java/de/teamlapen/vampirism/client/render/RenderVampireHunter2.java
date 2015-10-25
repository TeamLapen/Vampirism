package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireHunter2;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for the second type of hunter, who represents a player
 */
public class RenderVampireHunter2 extends RenderBiped {

    private static final ResourceLocation textureBase1 = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterBase1.png");
    private static final ResourceLocation textureBase2 = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterBase2.png");
    private static final ResourceLocation textureBase3 = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterBase3.png");
    private static final ResourceLocation textureBase5 = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterBase5.png");
    private static final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterExtra.png");
    private final ModelVampireHunter model;

    public RenderVampireHunter2() {
        super(new ModelVampireHunter(), 0.5F);
        model = (ModelVampireHunter) modelBipedMain;
    }

    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        renderModel((EntityVampireHunter2) p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
    }

    protected void renderModel(EntityVampireHunter2 hunter, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float f) {
        if (((hunter).getOutfit(0) & 111) != 0) {
            model.setSkipCloakOnce();
        }
        super.renderModel(hunter, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, f);
        bindTexture(textureExtra);
        if (hunter.shouldRenderDefaultWeapons()) {
            model.renderWeapons(f);
        }
        model.renderHat(f, hunter.getOutfit(1) % 2 + 1);



        String tex = hunter.getTextureName();
        bindPlayerTexture(tex);
        model.renderSecondHead(f);

    }


    @Override
    protected ResourceLocation getEntityTexture(EntityLiving hunter) {
        switch (((EntityVampireHunter2) hunter).getOutfit(0) & 11) {
            case 0:
                return textureBase1;
            case 2:
                return textureBase3;
            case 3:
                return textureBase5;
            default:
                return textureBase2;
        }
    }

    private void bindPlayerTexture(String username) {
        ResourceLocation location;
        if ("none".equals(username) || ((location = AbstractClientPlayer.getLocationSkin(username)) == null)) {
            location = textureBase3;
            //location = AbstractClientPlayer.getLocationSkin("steve");
        }
        AbstractClientPlayer.getDownloadImageSkin(location, username);
        this.bindTexture(location);
    }
}
