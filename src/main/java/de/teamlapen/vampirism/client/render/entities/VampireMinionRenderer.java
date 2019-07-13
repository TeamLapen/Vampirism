package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.client.model.BipedShrinkableModel;
import de.teamlapen.vampirism.entity.minions.vampire.VampireMinionBaseEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireMinionRenderer extends BipedRenderer<VampireMinionBaseEntity, BipedShrinkableModel<VampireMinionBaseEntity>> {

    private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public VampireMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BipedShrinkableModel(0F, 0F, 64, 64), 0.35F);
    }

    @Override
    protected ResourceLocation getEntityTexture(VampireMinionBaseEntity entity) {

        // Logger.i("test", ""+minion.getLord());
        IMinionLord lord = entity.getLord();
//        if (lord!=null&&MinionHelper.isLordPlayer(minion)) {
//            AbstractClientPlayer player = ((AbstractClientPlayer) (lord).getRepresentingEntity());
//            ResourceLocation skin = player.getLocationSkin();
//            ResourceLocation newSkin = new ResourceLocation("vampirism/temp/" + skin.hashCode());
//            TextureHelper.createVampireTexture(player, skin, newSkin);
//            return newSkin;
//        } TODO
        return texture;
    }

    protected ResourceLocation getVampireTexture(int id) {
        return BasicVampireRenderer.getVampireTexture(id);
    }

    @Override
    protected void renderModel(VampireMinionBaseEntity entity, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        float size = 0F;
        if (entity.getOldVampireTexture() != -1) {
            size = 1F - Math.min(entity.ticksExisted / 50F, 1F);
        }
        getEntityModel().setSize(size);

        // If either invisible or already small ->use parent method
        if (entity.isInvisible() || entity.getOldVampireTexture() == -1) {
            super.renderModel(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        } else {
            // firstly render own texture secondly blend old vampire texture in
            this.bindEntityTexture(entity);
            getEntityModel().render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GlStateManager.enableBlend();
            GlStateManager.color4f(1F, 1F, 1F, size);

            this.bindTexture(this.getVampireTexture(entity.getOldVampireTexture()));
            getEntityModel().render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GlStateManager.disableBlend();
        }
    }

}