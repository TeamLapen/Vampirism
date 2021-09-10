package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.mixin.client.AgeableModelAccessor;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PlayerBodyOverlayLayer<T extends MinionEntity<?> & IPlayerOverlay, M extends PlayerModel<T>> extends RenderLayer<T, M> {
    public PlayerBodyOverlayLayer(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = getTextureLocation(entitylivingbaseIn);
        if (entitylivingbaseIn.shouldRenderLordSkin()) {
            loc = entitylivingbaseIn.getOverlayPlayerProperties().map(Pair::getLeft).orElse(loc);
        }

        VertexConsumer vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(loc));
        ((AgeableModelAccessor) this.getParentModel()).getBodyParts_vampirism().forEach(
                b -> b.visible = true
        );
        ((AgeableModelAccessor) this.getParentModel()).getBodyParts_vampirism().forEach(b -> b.render(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1));
        ((AgeableModelAccessor) this.getParentModel()).getBodyParts_vampirism().forEach(
                b -> b.visible = false
        );
    }
}
