package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.client.renderer.entity.DualBipedRenderer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This layer is supposed to render the minion with its texture or the player's body texture instead.
 * <br>
 * In this implementation the minion must use a {@link de.teamlapen.vampirism.client.renderer.entity.layers.PlayerBodyOverlayLayer.VisibilityPlayerModel} model which allows this layer to render the model depending on the relevant parts without changing the visibility of the {@link ModelPart}s
 *
 * @param <T> The minion entity
 * @param <M> The no rendering dummy minion model
 */
public class PlayerBodyOverlayLayer<T extends MinionEntity<?> & IPlayerOverlay, M extends PlayerBodyOverlayLayer.VisibilityPlayerModel<T>> extends RenderLayer<T, M> {

    public PlayerBodyOverlayLayer(@NotNull DualBipedRenderer<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = getTextureLocation(entitylivingbaseIn);
        RenderType type = getParentModel().getRenderType(this.getParentModel(), texture, entitylivingbaseIn);

        if (entitylivingbaseIn.shouldRenderLordSkin()) {
            if (type != null) {
                getParentModel().setVisibility(VisibilityPlayerModel.Visibility.HEAD);
                getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(type), packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
            }

            texture = entitylivingbaseIn.getPlayerOverlay().map(s -> Minecraft.getInstance().getSkinManager().getInsecureSkin(s)).map(PlayerSkin::texture).orElse(texture);
            RenderType bodyType = getParentModel().getRenderType(this.getParentModel(), texture, entitylivingbaseIn);
            if (bodyType != null) {
                getParentModel().setVisibility(VisibilityPlayerModel.Visibility.BODY);
                getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(bodyType), packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
            }
        } else if (type != null) {
            getParentModel().setVisibility(VisibilityPlayerModel.Visibility.ALL);
            getParentModel().renderToBuffer(matrixStackIn, bufferIn.getBuffer(type), packedLightIn, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255, 255));
        }
        getParentModel().setVisibility(VisibilityPlayerModel.Visibility.NONE);
    }

    /**
     * Default {@link PlayerModel} implementation that allows to hide the head and body parts without changing the {@link ModelPart#visible} property.
     */
    public static class VisibilityPlayerModel<T extends MinionEntity<?>> extends PlayerModel<T> {

        private @NotNull Visibility visibility = Visibility.NONE;
        private final @NotNull Collection<ModelPart> hatList = Collections.singleton(super.hat);

        public VisibilityPlayerModel(ModelPart pRoot, boolean pSlim) {
            super(pRoot, pSlim);
        }

        @Override
        protected @NotNull Iterable<ModelPart> headParts() {
            if (this.visibility.head) {
                return Iterables.concat(super.headParts(), this.hatList);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        protected @NotNull Iterable<ModelPart> bodyParts() {
            if (this.visibility.body) {
                List<ModelPart> parts = Lists.newArrayList(super.bodyParts());
                parts.remove(this.hat);
                return parts;
            } else {
                return Collections.emptyList();
            }
        }

        public void setVisibility(@NotNull Visibility type) {
            this.visibility = type;
        }

        @Nullable
        public RenderType getRenderType(PlayerModel<T> model, ResourceLocation location, T entity) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean pBodyVisible = !entity.isInvisible();
            boolean translucent = !pBodyVisible && !entity.isInvisibleTo(minecraft.player);
            boolean flag2 = minecraft.shouldEntityAppearGlowing(entity);
            if (translucent) {
                return RenderType.itemEntityTranslucentCull(location);
            } else if (pBodyVisible) {
                return model.renderType(location);
            } else {
                return flag2 ? RenderType.outline(location) : null;
            }
        }

        public enum Visibility {
            HEAD(true, false),
            BODY(false, true),
            NONE(false, false),
            ALL(true, true);

            private final boolean head;
            private final boolean body;

            Visibility(boolean head, boolean body) {
                this.head = head;
                this.body = body;
            }
        }
    }
}
