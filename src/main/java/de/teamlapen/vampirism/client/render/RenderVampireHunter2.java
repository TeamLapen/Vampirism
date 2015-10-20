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

    private static final ResourceLocation textureNormal = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunter.png");

    public RenderVampireHunter2() {
        super(new ModelVampireHunter(true), 0.5F);
    }

    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        renderModel((EntityVampireHunter2) p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
    }

    protected void renderModel(EntityVampireHunter2 hunter, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        modelBipedMain.bipedHead.isHidden = true;
        super.renderModel(hunter, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        modelBipedMain.bipedHead.isHidden = false;
        String tex = hunter.getTextureName();
        bindPlayerTexture(tex);
        ((ModelVampireHunter) modelBipedMain).renderSecondHead(p_77036_7_);


    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving p_110775_1_) {
        return textureNormal;
    }

    private void bindPlayerTexture(String username) {
        ResourceLocation location;
        if ("none".equals(username) || ((location = AbstractClientPlayer.getLocationSkin(username)) == null)) {
            location = AbstractClientPlayer.getLocationSkin("steve");
        }
        AbstractClientPlayer.getDownloadImageSkin(location, username);
        this.bindTexture(location);
    }
}
