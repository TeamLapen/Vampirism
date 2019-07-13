package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.client.render.RenderUtil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerVampirePlayerHead extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final ResourceLocation[] eyeOverlays;
    private final ResourceLocation[] fangOverlays;

    public LayerVampirePlayerHead(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
        eyeOverlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/eyes" + (i) + ".png");
        }
        fangOverlays = new ResourceLocation[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < fangOverlays.length; i++) {
            fangOverlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/fangs" + i + ".png");
        }
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!VampirismConfig.CLIENT.renderVampireEyes.get()) return;
        VampirePlayer vampirePlayer = VampirePlayer.get(player);
        if (vampirePlayer.getLevel() > 0 && !vampirePlayer.isDisguised() && !player.isInvisible()) {
            int eyeType = Math.max(0, Math.min(vampirePlayer.getEyeType(), eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(vampirePlayer.getFangType(), fangOverlays.length - 1));
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            bindTexture(fangOverlays[fangType]);
            getEntityModel().bipedHead.render(scale);

            if (vampirePlayer.getGlowingEyes()) {
                bindTexture(eyeOverlays[eyeType]);
                RenderUtil.renderGlowing(getEntityModel().bipedHead, 240f, player, scale);

            } else {
                renderNormalEyes(eyeType, scale);
            }

            GlStateManager.popMatrix();

        }
    }


    public boolean shouldCombineTextures() {
        return true;
    }


    private void renderNormalEyes(int eyeType, float scale) {
        bindTexture(eyeOverlays[eyeType]);
        getEntityModel().bipedHead.render(scale);
    }
}