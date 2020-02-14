package de.teamlapen.vampirism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
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
    public void render(MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractClientPlayerEntity player, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!VampirismConfig.CLIENT.renderVampireEyes.get() || !player.isAlive()) return;
        VampirePlayer vampirePlayer = VampirePlayer.get(player);
        if (vampirePlayer.getLevel() > 0 && !vampirePlayer.isDisguised() && !player.isInvisible()) {
            int eyeType = Math.max(0, Math.min(vampirePlayer.getEyeType(), eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(vampirePlayer.getFangType(), fangOverlays.length - 1));
            IVertexBuilder vertexBuilderEye = iRenderTypeBuffer.getBuffer(RenderType.entitySolid(eyeOverlays[eyeType]));
            IVertexBuilder vertexBuilderFang = iRenderTypeBuffer.getBuffer(RenderType.entitySolid(fangOverlays[fangType]));
            int packerOverlay = LivingRenderer.getPackedOverlay(player, 0);
            ModelRenderer head = this.getEntityModel().bipedHead;
            head.rotationPointX = 0.0F;
            head.rotationPointY = 0.0F;
            head.render(matrixStack, vertexBuilderFang, i, packerOverlay);
            head.render(matrixStack, vertexBuilderEye, i, packerOverlay);

        }

    }

}