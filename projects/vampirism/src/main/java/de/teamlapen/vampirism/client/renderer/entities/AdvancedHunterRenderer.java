package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.hunter.AdvancedHunterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.Nullable;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
public class AdvancedHunterRenderer extends DualSplitBipedRenderer<AdvancedHunterEntity, AdvancedHunterRenderer.AdvancedHunterRenderState, ClothedModel<AdvancedHunterRenderer.AdvancedHunterRenderState>> {
    private static final PlayerSkin FALLBACK = new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("fallback"), VIdentifier.mod("textures/entity/hunter_base1.png")), null, null, PlayerModelType.WIDE, false);
    private final PlayerSkin[] textures;


    public AdvancedHunterRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.PLAYER, ModelLayers.PLAYER_SLIM, ClothedModel::new, 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<AdvancedHunterRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)),
                context.getEquipmentRenderer()));
        this.textures = gatherTextures("textures/entity/hunter", true);
    }

    @Override
    protected boolean splitRenderingEnabled() {
        return ModConfig.client().renderAdvancedMobPlayerFaces.get();
    }

    @Override
    protected Identifier getTexture(AdvancedHunterRenderState renderState, RenderPart part) {
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
    protected void submitNameDisplay(AdvancedHunterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.distanceToCameraSq <= 256) {
            super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public AdvancedHunterRenderState createRenderState() {
        return new AdvancedHunterRenderState();
    }

    @Override
    public void extractRenderState(AdvancedHunterEntity entity, AdvancedHunterRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        state.skin = this.textures.length == 0 ? FALLBACK : textures[entity.getBodyTexture() % textures.length];
        state.hasCloak = entity.hasCloak();
        state.overlay = entity.getPlayerOverlay().map(PlayerSkinRenderCache.RenderInfo::playerSkin).map(PlayerSkin::body).map(ClientAsset.Texture::texturePath).orElse(null);
    }

    public static class AdvancedHunterRenderState extends AvatarLikeRenderState implements IOverlayRenderState {
        public boolean hasCloak;

        @Nullable
        public Identifier overlay;

        @Nullable
        @Override
        public Identifier overlay() {
            return this.overlay;
        }
    }
}
