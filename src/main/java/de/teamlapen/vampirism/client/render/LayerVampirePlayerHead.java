package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerVampirePlayerHead implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;

    private final ResourceLocation[] overlays;

    public LayerVampirePlayerHead(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        overlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < overlays.length; i++) {
            overlays[i] = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vanilla/steve" + (i + 1) + ".png");
        }
    }

    public void doRenderLayer(AbstractClientPlayer player, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        VampirePlayer vampirePlayer = VampirePlayer.get(player);
        if (vampirePlayer.getLevel() > 0 && !vampirePlayer.isDisguised() && !player.isInvisible()) {
            int type = vampirePlayer.getEyeType();
            this.playerRenderer.bindTexture(overlays[type]);

            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            this.playerRenderer.getMainModel().bipedHead.render(scale);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}