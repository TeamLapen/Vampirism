package de.teamlapen.vampirism.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entity.layers.AdvancedVampireEyeLayer;
import de.teamlapen.vampirism.client.renderer.entity.layers.PlayerFaceOverlayLayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Render the advanced vampire with overlays
 */
@OnlyIn(Dist.CLIENT)
public class AdvancedVampireRenderer extends HumanoidMobRenderer<AdvancedVampireEntity, HumanoidModel<AdvancedVampireEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/advanced_vampire.png");

    public AdvancedVampireRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), 0.5F);
        if (VampirismConfig.CLIENT.renderAdvancedMobPlayerFaces.get()) {
            this.addLayer(new PlayerFaceOverlayLayer<>(this));
            this.addLayer(new AdvancedVampireEyeLayer(this));

        }
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull AdvancedVampireEntity entity) {
        return texture;
    }


    @Override
    protected void renderNameTag(@NotNull AdvancedVampireEntity entityIn, @NotNull Component displayNameIn, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        double dist = this.entityRenderDispatcher.distanceToSqr(entityIn);
        if (dist <= 256) {
            super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
