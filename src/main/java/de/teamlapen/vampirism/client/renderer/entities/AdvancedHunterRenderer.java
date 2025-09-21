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
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedHunterRenderer extends DualBipedRenderer<AdvancedHunterEntity, AdvancedHunterRenderer.AdvancedHunterRenderState, BasicHunterModel<AdvancedHunterRenderer.AdvancedHunterRenderState>> {
    private static final ResourceLocation textureCloak = VResourceLocation.mod("textures/entity/hunter_cloak.png");
    private static final PlayerSkin FALLBACK = new PlayerSkin(VResourceLocation.mod("textures/entity/hunter_base1.png"), null, null, null, PlayerSkin.Model.WIDE, false);
    private final @NotNull PlayerSkin[] textures;


    public AdvancedHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), true), 0.5F);
        this.addLayer(new CloakLayer<>(this, textureCloak, s -> s.hasCloak));
        this.addLayer(new ArmorLayer<HumanoidModel<AdvancedHunterRenderState>>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getEquipmentRenderer()));
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
    protected void renderNameTag(AdvancedHunterRenderState state, @NotNull Component displayName, @NotNull PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLightIn) {
        if (state.distanceToCameraSq <= 256) {
            super.renderNameTag(state, displayName, stack, bufferSource, packedLightIn);
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
        state.overlayTexture = entity.getPlayerOverlay().map(p -> Minecraft.getInstance().getSkinManager().getInsecureSkin(p)).map(PlayerSkin::texture).orElseGet(DefaultPlayerSkin::getDefaultTexture);
    }

    public static class AdvancedHunterRenderState extends PlayerRenderState implements IOverlayRenderState {
        public boolean hasCloak;
        public ResourceLocation overlayTexture;

        @Override
        public ResourceLocation overlay() {
            return this.overlayTexture;
        }
    }
}
