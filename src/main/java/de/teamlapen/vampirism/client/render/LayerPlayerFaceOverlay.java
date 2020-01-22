package de.teamlapen.vampirism.client.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Renders an overlay over the entities face
 */
@OnlyIn(Dist.CLIENT)
public class LayerPlayerFaceOverlay<T extends MobEntity & IPlayerFace, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    private final BipedRenderer<T, M> renderBiped;

    public LayerPlayerFaceOverlay(BipedRenderer<T, M> renderBiped) {
        super(renderBiped);
        this.renderBiped = renderBiped;
    }

    @Override
    public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T entityIn, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {//render
        ResourceLocation loc = DefaultPlayerSkin.getDefaultSkinLegacy();
        GameProfile prof = entityIn.getPlayerFaceProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getInstance().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        }

        renderBiped.bindTexture(loc);
        RenderSystem.pushMatrix();
        if (entityIn.func_225608_bj_()) {//isSneaking
            RenderSystem.translatef(0.0F, 0.2F, 0.0F);
        }
        GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);

        (this.renderBiped.getEntityModel()).bipedHead.render(scale);
        (this.renderBiped.getEntityModel()).bipedHeadwear.render(scale);
        GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);

        RenderSystem.popMatrix();
    }

}
