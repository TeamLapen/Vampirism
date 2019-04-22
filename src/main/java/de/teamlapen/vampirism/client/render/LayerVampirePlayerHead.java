package de.teamlapen.vampirism.client.render;

import de.teamlapen.lib.lib.client.render.RenderUtil;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerVampirePlayerHead implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    private final ResourceLocation[] eyeOverlays;
    private final ResourceLocation[] fangOverlays;

    public LayerVampirePlayerHead(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
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
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Configs.disable_vampireEyes) return;
        VampirePlayer vampirePlayer = VampirePlayer.get(player);
        if (vampirePlayer.getLevel() > 0 && !vampirePlayer.isDisguised() && !player.isInvisible()) {
            int eyeType = Math.max(0, Math.min(vampirePlayer.getEyeType(), eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(vampirePlayer.getFangType(), fangOverlays.length - 1));
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.playerRenderer.bindTexture(fangOverlays[fangType]);
            this.playerRenderer.getMainModel().bipedHead.render(scale);

            if (vampirePlayer.getGlowingEyes()) {
                RenderUtil.renderGlowing(playerRenderer, playerRenderer.getMainModel().bipedHead, eyeOverlays[eyeType], 240f, player, scale);

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
        this.playerRenderer.bindTexture(eyeOverlays[eyeType]);
        this.playerRenderer.getMainModel().bipedHead.render(scale);


    }
}