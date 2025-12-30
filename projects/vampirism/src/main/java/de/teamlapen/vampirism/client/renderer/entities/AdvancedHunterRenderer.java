package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerFaceOverlayLayer;
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
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
public class AdvancedHunterRenderer extends DualBipedRenderer<AdvancedHunterEntity, AdvancedHunterRenderer.AdvancedHunterRenderState, ClothedModel<AdvancedHunterRenderer.AdvancedHunterRenderState>> {
    private static final PlayerSkin FALLBACK = new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mod("fallback"), VIdentifier.mod("textures/entity/hunter_base1.png")), null, null, PlayerModelType.WIDE, false);
    private final @NotNull PlayerSkin[] textures;


    public AdvancedHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false), new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
        this.addLayer(new ArmorLayer<HumanoidModel<AdvancedHunterRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)),
                context.getEquipmentRenderer()));
        if (ModConfig.client().renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false)));
            this.getModel().head.visible = false;
            this.getModel().hat.visible = false;
        }
        this.textures = gatherTextures("textures/entity/hunter", true);
    }

    @Override
    protected PlayerSkin determineTextureAndModel(@NotNull AdvancedHunterRenderState entity) {
        return entity.skin;
    }

    @Override
    protected void submitNameTag(AdvancedHunterRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 256) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    @Override
    public @NotNull AdvancedHunterRenderState createRenderState() {
        return new AdvancedHunterRenderState();
    }

    @Override
    public void extractRenderState(@NotNull AdvancedHunterEntity entity, @NotNull AdvancedHunterRenderState state, float p_363123_) {
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
