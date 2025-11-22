package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.AdvancedVampireFangLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.IOverlayRenderState;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.common.util.TextureComparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
public class AdvancedVampireRenderer extends HumanoidMobRenderer<AdvancedVampireEntity, AdvancedVampireRenderer.AdvancedVampireRenderState, HumanoidModel<AdvancedVampireRenderer.AdvancedVampireRenderState>> {
    private final ResourceLocation texture = VResourceLocation.mod("textures/entity/advanced_vampire.png");
    private final ResourceLocation @NotNull [] textures;


    public AdvancedVampireRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new ClothedModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), 0.5F);
        if (ModConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.addLayer(new AdvancedVampireEyeLayer(this));
            this.addLayer(new AdvancedVampireFangLayer(this));

        }
        this.textures = Minecraft.getInstance().getResourceManager().listResources("textures/entity/vampire", s -> s.getPath().endsWith(".png")).keySet().stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).sorted(TextureComparator.alphaNumericComparator()).toArray(ResourceLocation[]::new);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull AdvancedVampireRenderState entity) {
        return entity.bodyTexture;
    }

    @Override
    public @NotNull AdvancedVampireRenderState createRenderState() {
        return new AdvancedVampireRenderState();
    }

    @Override
    public void extractRenderState(@NotNull AdvancedVampireEntity entity, @NotNull AdvancedVampireRenderState renderState, float p_363123_) {
        super.extractRenderState(entity, renderState, p_363123_);
        renderState.bodyTexture = this.textures.length == 0 ? this.texture : this.textures[entity.getBodyTexture() % this.textures.length];
        renderState.fangType = entity.getFangType();
        renderState.eyeType = entity.getEyeType();
        renderState.overlay = entity.getPlayerOverlay().map(PlayerSkinRenderCache.RenderInfo::playerSkin).map(net.minecraft.world.entity.player.PlayerSkin::body).map(ClientAsset.Texture::texturePath).orElse(null);
    }

    @Override
    protected void submitNameTag(AdvancedVampireRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 256) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    public static class AdvancedVampireRenderState extends AvatarRenderState implements IOverlayRenderState {
        public ResourceLocation bodyTexture;
        public int fangType;
        public int eyeType;
        public ResourceLocation overlay;

        @Override
        public ResourceLocation overlay() {
            return this.overlay;
        }
    }
}
