package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.client.model.ModelBipedShrinkable;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionBase;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderVampireMinion extends RenderBiped<EntityVampireMinionBase> {

    private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampire.png");

    public RenderVampireMinion(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBipedShrinkable(0F, 0F, 64, 64), 0.35F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVampireMinionBase entity) {

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
        return RenderBasicVampire.getVampireTexture(id);
    }

    @Override
    protected void renderModel(EntityVampireMinionBase entity, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        float size = 0F;
        if (entity.getOldVampireTexture() != -1) {
            size = 1F - Math.min(entity.ticksExisted / 50F, 1F);
        }
        ((ModelBipedShrinkable) this.mainModel).setSize(size);

        // If either invisible or already small ->use parent method
        if (entity.isInvisible() || entity.getOldVampireTexture() == -1) {
            super.renderModel(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        } else {
            // firstly render own texture secondly blend old vampire texture in
            this.bindEntityTexture(entity);
            this.mainModel.render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GlStateManager.enableBlend();
            GlStateManager.color4f(1F, 1F, 1F, size);

            this.bindTexture(this.getVampireTexture(entity.getOldVampireTexture()));
            this.mainModel.render(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
            GlStateManager.disableBlend();
        }
    }

}