package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.common.entity.VampirismEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HunterTrainerRenderer extends HumanoidMobRenderer<VampirismEntity, HunterTrainerRenderer.HunterTrainerRenderState, ClothedModel<HunterTrainerRenderer.HunterTrainerRenderState>> {

    private final ResourceLocation texture = VResourceLocation.mod("textures/entity/hunter_trainer.png");

    public HunterTrainerRenderer(EntityRendererProvider.@NotNull Context context, boolean renderEquipment) {
        super(context, new ClothedModel<>(context.bakeLayer(ModEntitiesRender.GENERIC_BIPED), false), 0.5F);
        if (renderEquipment) {
            this.addLayer(new HumanoidArmorLayer<>(this, ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), HumanoidModel::new), context.getEquipmentRenderer()));
        }
    }


    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull HunterTrainerRenderState entity) {
        return texture;
    }

    @Override
    protected void submitNameTag(HunterTrainerRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 128) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    @Override
    public @NotNull HunterTrainerRenderState createRenderState() {
        return new HunterTrainerRenderState();
    }

    public static class HunterTrainerRenderState extends AvatarRenderState {
    }
}
