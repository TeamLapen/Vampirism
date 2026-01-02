package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireFangLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;

/**
 * Render the advanced vampire with overlays
 */
public class AdvancedVampireRenderer extends DualBipedRenderer<AdvancedVampireEntity, AdvancedVampireRenderer.AdvancedVampireRenderState, ClothedModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {
    private static final PlayerSkin FALLBACK = new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("fallback"), VIdentifier.mod("textures/entity/advanced_vampire.png")), null, null, PlayerModelType.WIDE, false);
    private final PlayerSkin[] textures;


    public AdvancedVampireRenderer(EntityRendererProvider.Context context) {
        super(context, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        if (ModConfig.client().renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false)));
            this.addLayer(new AdvancedVampireEyeLayer(this));
            this.addLayer(new AdvancedVampireFangLayer(this));
        }
        this.textures = gatherTextures("textures/entity/vampire", true);
    }

    @Override
    protected PlayerSkin determineTextureAndModel(AdvancedVampireRenderer.AdvancedVampireRenderState entity) {
        return entity.skin;
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
    protected void submitNameTag(AdvancedVampireRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 256) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
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
