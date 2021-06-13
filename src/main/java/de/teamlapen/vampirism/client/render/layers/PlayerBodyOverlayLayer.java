package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.mixin.client.AgeableModelAccessor;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class PlayerBodyOverlayLayer<T extends MinionEntity<?> & IPlayerOverlay, M extends PlayerModel<T>> extends LayerRenderer<T, M> {
    public PlayerBodyOverlayLayer(IEntityRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation loc = getEntityTexture(entitylivingbaseIn);
        if (entitylivingbaseIn.shouldRenderLordSkin()) {
            loc = entitylivingbaseIn.getOverlayPlayerProperties().map(Pair::getLeft).orElse(loc);
        }

        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(loc));
        ((AgeableModelAccessor) this.getEntityModel()).getBodyParts_vampirism().forEach(
                b -> b.showModel = true
        );
        ((AgeableModelAccessor) this.getEntityModel()).getBodyParts_vampirism().forEach(b -> b.render(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1));
        ((AgeableModelAccessor) this.getEntityModel()).getBodyParts_vampirism().forEach(
                b -> b.showModel = false
        );
    }
}
