package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
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
public class VampirePlayerHeadLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final ResourceLocation[] eyeOverlays;
    private final ResourceLocation[] fangOverlays;

    public VampirePlayerHeadLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
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
    public void render(MatrixStack stack, IRenderTypeBuffer iRenderTypeBuffer, int i, AbstractClientPlayerEntity player, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!VampirismConfig.CLIENT.renderVampireEyes.get() || !player.isAlive()) return;
        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        if (atts.vampireLevel > 0 && !atts.getVampSpecial().disguised && !player.isInvisible()) {
            int eyeType = Math.max(0, Math.min(atts.getVampSpecial().eyeType, eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(atts.getVampSpecial().fangType, fangOverlays.length - 1));
            RenderType eyeRenderType = atts.getVampSpecial().glowingEyes ? RenderType.getEyes(eyeOverlays[eyeType]) : RenderType.getEntityCutoutNoCull(eyeOverlays[eyeType]);
            IVertexBuilder vertexBuilderEye = iRenderTypeBuffer.getBuffer(eyeRenderType);
            int packerOverlay = LivingRenderer.getPackedOverlay(player, 0);
            ModelRenderer head = this.getEntityModel().bipedHead;
            head.render(stack, vertexBuilderEye, i, packerOverlay);
            IVertexBuilder vertexBuilderFang = iRenderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(fangOverlays[fangType]));
            head.render(stack, vertexBuilderFang, i, packerOverlay);


        }

    }

}