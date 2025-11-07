package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.BasicHunterModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.CloakLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.hunter.AdvancedHunterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
public class AdvancedHunterRenderer extends DualBipedRenderer<AdvancedHunterEntity, AdvancedHunterRenderer.AdvancedHunterRenderState, BasicHunterModel<AdvancedHunterRenderer.AdvancedHunterRenderState>> {
    private static final ResourceLocation textureCloak = VResourceLocation.mod("textures/entity/hunter_cloak.png");
    private static final PlayerSkin FALLBACK = new PlayerSkin(new ClientAsset.ResourceTexture(VResourceLocation.mod("fallback"), VResourceLocation.mod("textures/entity/hunter_base1.png")), null, null, PlayerModelType.WIDE, false);
    private final @NotNull PlayerSkin[] textures;


    public AdvancedHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), true), 0.5F);
        this.addLayer(new CloakLayer<>(this, textureCloak, s -> s.hasCloak));
        this.addLayer(new ArmorLayer<HumanoidModel<AdvancedHunterRenderState>>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, context.getModelSet(), x -> new BasicHunterModel<>(x, true)),
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new BasicHunterModel<>(x, false)),
                context.getEquipmentRenderer()));
        if (ModConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.getModel().head.visible = false;
            this.getModel().hat.visible = false;
            this.textures = gatherTextures("textures/entity/hunter", true);
        } else {
            this.textures = new PlayerSkin[]{};
        }
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
        state.overlayTexture = entity.getPlayerOverlay().map(PlayerSkinRenderCache.RenderInfo::playerSkin).map(PlayerSkin::body).map(ClientAsset.Texture::texturePath).orElse(null);
    }

    public static class AdvancedHunterRenderState extends AvatarRenderState implements IOverlayRenderState {
        public boolean hasCloak;

        @Nullable
        public ResourceLocation overlayTexture;

        @Override
        public ResourceLocation overlay() {
            return this.overlayTexture;
        }
    }
}
