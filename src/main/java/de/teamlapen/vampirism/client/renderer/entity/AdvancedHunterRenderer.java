package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.hunter.IAdvancedHunter;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.renderer.entity.layers.CloakLayer;
import de.teamlapen.vampirism.client.renderer.entity.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.util.PlayerModelType;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer for the advanced hunter.
 * Similar to {@link BasicHunterRenderer}
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedHunterRenderer extends DualBipedRenderer<AdvancedHunterEntity, BasicHunterModel<AdvancedHunterEntity>> {
    private static final ResourceLocation textureCloak = new ResourceLocation(REFERENCE.MODID + ":textures/entity/hunter_cloak.png");
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_base1.png");
    private final Pair<ResourceLocation, PlayerModelType> @NotNull [] textures;


    public AdvancedHunterRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), false), new BasicHunterModel<>(context.bakeLayer(ModEntitiesRender.HUNTER), true), 0.5F);
        this.addLayer(new CloakLayer<>(this, textureCloak, IAdvancedHunter::hasCloak));
        this.addLayer(new ArmorLayer<HumanoidModel<AdvancedHunterEntity>>(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.getModel().head.visible = false;
            this.getModel().hat.visible = false;
            this.textures = gatherTextures("textures/entity/hunter", true);
        } else {
            //noinspection unchecked
            this.textures = new Pair[]{};
        }
    }

    @Override
    protected Pair<ResourceLocation, PlayerModelType> determineTextureAndModel(@NotNull AdvancedHunterEntity entity) {
        if (this.textures.length == 0) return Pair.of(texture, PlayerModelType.WIDE);
        return this.textures[entity.getBodyTexture() % this.textures.length];
    }

    @Override
    protected void renderNameTag(@NotNull AdvancedHunterEntity entityIn, @NotNull Component displayNameIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, float partialTicks) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 256) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn, packedLightIn);
        }
    }
}
