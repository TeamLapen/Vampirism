package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.renderer.entities.state.AvatarLikeRenderState;
import de.teamlapen.vampirism.common.world.entity.VampirismEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class HunterTrainerRenderer extends HumanoidMobRenderer<VampirismEntity, HunterTrainerRenderer.HunterTrainerRenderState, ClothedModel<HunterTrainerRenderer.HunterTrainerRenderState>> {

    private final Identifier texture = VIdentifier.mod("textures/entity/hunter_trainer.png");

    public HunterTrainerRenderer(EntityRendererProvider.@NotNull Context context, boolean renderEquipment) {
        super(context, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        if (renderEquipment) {
            this.addLayer(new HumanoidArmorLayer<>(this, ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, context.getModelSet(), x -> new ClothedModel<>(x, false)), context.getEquipmentRenderer()));
        }
    }


    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull HunterTrainerRenderState entity) {
        return texture;
    }

    @Override
    protected void submitNameTag(HunterTrainerRenderState renderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodeCollector, @NotNull CameraRenderState cameraRenderState) {
        if (renderState.distanceToCameraSq <= 128) {
            super.submitNameTag(renderState, poseStack, nodeCollector, cameraRenderState);
        }
    }

    @Override
    public @NotNull HunterTrainerRenderState createRenderState() {
        return new HunterTrainerRenderState();
    }

    public static class HunterTrainerRenderState extends AvatarLikeRenderState {
    }
}
