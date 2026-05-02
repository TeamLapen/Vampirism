package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireFangLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;

/**
 * Render the advanced vampire with overlays
 */
public class AdvancedVampireRenderer extends DualSplitBipedRenderer<AdvancedVampireEntity, AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {
    private static final PlayerSkin FALLBACK = new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("fallback"), VIdentifier.mod("textures/entity/advanced_vampire.png")), null, null, PlayerModelType.WIDE, false);
    private final PlayerSkin[] textures;


    public AdvancedVampireRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PLAYER, ModelLayers.PLAYER_SLIM, ClothedModel::new, 0.5F);
        if (ModConfig.client().renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new AdvancedVampireEyeLayer(this));
            this.addLayer(new AdvancedVampireFangLayer(this));
        }
        this.textures = gatherTextures("textures/entity/vampire", true);
    }

    @Override
    protected boolean splitRenderingEnabled() {
        return ModConfig.client().renderAdvancedMobPlayerFaces.get();
    }

    @Override
    protected Identifier getTexture(AdvancedVampireRenderer.AdvancedVampireRenderState renderState, RenderPart part) {
        Identifier texture = null;
        if (part == RenderPart.HEAD) {
            texture = renderState.overlay;
        }
        if (texture != null) {
            return texture;
        }
        return renderState.skin.body().texturePath();
    }

    @Override
    public AdvancedVampireRenderState createRenderState() {
        return new AdvancedVampireRenderState();
    }

    @Override
    public void extractRenderState(AdvancedVampireEntity entity, AdvancedVampireRenderState renderState, float p_363123_) {
        super.extractRenderState(entity, renderState, p_363123_);
        renderState.skin = this.textures.length == 0 ? FALLBACK : this.textures[entity.getBodyTexture() % this.textures.length];
        renderState.fangType = entity.getFangType();
        renderState.eyeType = entity.getEyeType();
        renderState.overlay = entity.getPlayerOverlay().map(PlayerSkinRenderCache.RenderInfo::playerSkin).map(PlayerSkin::body).map(ClientAsset.Texture::texturePath).orElse(null);
    }

    @Override
    protected void submitNameDisplay(AdvancedVampireRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.distanceToCameraSq <= 256) {
            super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
        }
    }

    public static class AdvancedVampireRenderState extends AvatarLikeRenderState implements IOverlayRenderState {
        public int fangType;
        public int eyeType;

        @Nullable
        public Identifier overlay;

        @Nullable
        @Override
        public Identifier overlay() {
            return this.overlay;
        }
    }
}
