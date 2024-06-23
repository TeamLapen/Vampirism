package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class VampirePlayerHeadLayer<T extends Player, Q extends HumanoidModel<T>> extends RenderLayer<T, Q> {

    private final ResourceLocation @NotNull [] eyeOverlays;
    private final ResourceLocation @NotNull [] fangOverlays;

    public VampirePlayerHeadLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        eyeOverlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + (i) + ".png");
        }
        fangOverlays = new ResourceLocation[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < fangOverlays.length; i++) {
            fangOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/fangs" + i + ".png");
        }
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, @NotNull T player, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!VampirismConfig.CLIENT.renderVampireEyes.get() || !player.isAlive()) return;
        VampirismPlayerAttributes atts = VampirismPlayerAttributes.get(player);
        if (atts.vampireLevel > 0 && !atts.getVampSpecial().disguised && !player.isInvisible()) {
            int eyeType = Math.max(0, Math.min(atts.getVampSpecial().eyeType, eyeOverlays.length - 1));
            int fangType = Math.max(0, Math.min(atts.getVampSpecial().fangType, fangOverlays.length - 1));
            RenderType eyeRenderType = atts.getVampSpecial().glowingEyes ? RenderType.eyes(eyeOverlays[eyeType]) : RenderType.entityCutoutNoCull(eyeOverlays[eyeType]);
            VertexConsumer vertexBuilderEye = iRenderTypeBuffer.getBuffer(eyeRenderType);
            int packerOverlay = LivingEntityRenderer.getOverlayCoords(player, 0);
            ModelPart head = this.getParentModel().head;
            head.render(stack, vertexBuilderEye, i, packerOverlay);
            VertexConsumer vertexBuilderFang = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(fangOverlays[fangType]));
            head.render(stack, vertexBuilderFang, i, packerOverlay);


        }

    }

}