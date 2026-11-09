package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
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

    private static final float TEXTURE_SIZE = 64.0F;
    private static final float MODEL_PIXEL = 1.0F / 16.0F;
    private static final float SURFACE_GAP = 0.01F;

    private final ResourceLocation @NotNull [] eyeOverlays;
    private final ResourceLocation @NotNull [] fangOverlays;

    public VampirePlayerHeadLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn) {
        super(entityRendererIn);
        eyeOverlays = new ResourceLocation[REFERENCE.EYE_TYPE_COUNT];
        for (int i = 0; i < eyeOverlays.length; i++) {
            eyeOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/eyes" + REFERENCE.eyeTextureId(i) + ".png");
        }
        fangOverlays = new ResourceLocation[REFERENCE.FANG_TYPE_COUNT];
        for (int i = 0; i < fangOverlays.length; i++) {
            fangOverlays[i] = VResourceLocation.mod("textures/entity/vanilla/fangs" + i + ".png");
        }
    }

    @Override
    public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, @NotNull T player, float v, float v1, float v2, float v3, float v4, float v5) {
        if (!VampirismConfig.CLIENT.renderVampireEyes.get() || !player.isAlive()) return;
        VampirismPlayerAttributes attributes = VampirismPlayerAttributes.get(player);
        if (attributes.vampireLevel <= 0 || attributes.getVampSpecial().disguised || player.isInvisible()) return;

        VampirePlayerSpecialAttributes appearance = attributes.getVampSpecial();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(player, 0);
        ModelPart head = this.getParentModel().head;
        // Keep the overlay on the base face so the outer skin layer can cover it.
        float frontOffset = SURFACE_GAP;

        if (appearance.eyeType >= 0) {
            int eyeType = Math.min(appearance.eyeType, this.eyeOverlays.length - 1);
            VampireFaceOverlay.SpritePair eyeSprite = VampireFaceOverlay.eye(eyeType);
            if (eyeSprite != null) {
                RenderType eyeRenderType = appearance.glowingEyes
                        ? RenderType.eyes(this.eyeOverlays[eyeType])
                        : RenderType.entityCutoutNoCull(this.eyeOverlays[eyeType]);
                VertexConsumer eyes = iRenderTypeBuffer.getBuffer(eyeRenderType);
                if (appearance.eyeX >= 0 && appearance.eyeY >= 0) {
                    this.renderPair(stack, eyes, i, packedOverlay, head, eyeSprite, appearance.eyeX, appearance.eyeY, frontOffset);
                } else {
                    head.render(stack, eyes, i, packedOverlay);
                }
            }
        }

        if (appearance.fangType >= 0) {
            int fangType = Math.min(appearance.fangType, this.fangOverlays.length - 1);
            VampireFaceOverlay.SpriteBounds fangSprite = VampireFaceOverlay.fangMotif(fangType);
            if (fangSprite != null) {
                VertexConsumer fangs = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(this.fangOverlays[fangType]));
                if (appearance.fangX >= 0 && appearance.fangY >= 0) {
                    this.renderMotif(stack, fangs, i, packedOverlay, head, fangSprite, appearance.fangX, appearance.fangY, frontOffset);
                } else {
                    head.render(stack, fangs, i, packedOverlay);
                }
            }
        }
    }

    private void renderPair(@NotNull PoseStack stack, @NotNull VertexConsumer vertices, int packedLight, int packedOverlay,
                            @NotNull ModelPart head, VampireFaceOverlay.SpritePair pair, int x, int y, float frontOffset) {
        if (pair == null || !head.visible || x < 0 || y < 0) return;
        x = Math.min(x, 4 - pair.width());
        y = Math.min(y, 8 - pair.height());

        stack.pushPose();
        head.translateAndRotate(stack);
        this.renderSprite(stack, vertices, packedLight, packedOverlay, pair.left(), x, y, frontOffset);
        this.renderSprite(stack, vertices, packedLight, packedOverlay,
                pair.right(), 8 - x - pair.right().width(), y, frontOffset);
        stack.popPose();
    }

    private void renderMotif(@NotNull PoseStack stack, @NotNull VertexConsumer vertices, int packedLight, int packedOverlay,
                             @NotNull ModelPart head, VampireFaceOverlay.SpriteBounds motif, int x, int y, float frontOffset) {
        if (!head.visible || x < 0 || y < 0) return;
        x = Math.min(x, 8 - motif.width());
        y = Math.min(y, 8 - motif.height());

        stack.pushPose();
        head.translateAndRotate(stack);
        this.renderSprite(stack, vertices, packedLight, packedOverlay, motif, x, y, frontOffset);
        stack.popPose();
    }

    private void renderSprite(@NotNull PoseStack stack, @NotNull VertexConsumer vertices, int packedLight, int packedOverlay,
                              VampireFaceOverlay.SpriteBounds source, int destinationX, int destinationY, float frontOffset) {
        float left = (destinationX - 4) * MODEL_PIXEL;
        float right = (destinationX + source.width() - 4) * MODEL_PIXEL;
        float top = (destinationY - 8) * MODEL_PIXEL;
        float bottom = (destinationY + source.height() - 8) * MODEL_PIXEL;
        float front = (-4 - frontOffset) * MODEL_PIXEL;
        float u0 = source.x() / TEXTURE_SIZE;
        float u1 = (source.x() + source.width()) / TEXTURE_SIZE;
        float v0 = source.y() / TEXTURE_SIZE;
        float v1 = (source.y() + source.height()) / TEXTURE_SIZE;

        this.addVertex(stack, vertices, left, bottom, front, u0, v1, packedLight, packedOverlay);
        this.addVertex(stack, vertices, right, bottom, front, u1, v1, packedLight, packedOverlay);
        this.addVertex(stack, vertices, right, top, front, u1, v0, packedLight, packedOverlay);
        this.addVertex(stack, vertices, left, top, front, u0, v0, packedLight, packedOverlay);
    }

    private void addVertex(@NotNull PoseStack stack, @NotNull VertexConsumer vertices, float x, float y, float z,
                           float u, float v, int packedLight, int packedOverlay) {
        vertices.addVertex(stack.last().pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(stack.last(), 0.0F, 0.0F, -1.0F);
    }
}
