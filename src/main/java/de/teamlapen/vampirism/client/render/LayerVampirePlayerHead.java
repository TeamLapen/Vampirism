package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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

    public void doRenderLayer(AbstractClientPlayer player, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
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

            //renderGlowingEyes(player,eyeType,partialTicks,scale);
            renderNormalEyes(eyeType, scale);

            GlStateManager.popMatrix();

        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }

    /**
     * Fix or delete
     */
    private void renderGlowingEyes(EntityPlayer player, int eyeType, float partialTicks, float scale) {
        this.playerRenderer.bindTexture(eyeOverlays[eyeType]);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        if (player.isInvisible()) {
            GlStateManager.depthMask(false);
        } else {
            GlStateManager.depthMask(true);
        }

        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.playerRenderer.getMainModel().bipedHead.render(scale);
        i = player.getBrightnessForRender();
        j = i % 65536;
        k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

    }

    private void renderNormalEyes(int eyeType, float scale) {
        this.playerRenderer.bindTexture(eyeOverlays[eyeType]);
        this.playerRenderer.getMainModel().bipedHead.render(scale);


    }
}