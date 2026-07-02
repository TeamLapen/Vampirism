package de.teamlapen.vampirism.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.renderer.entities.layers.TaskMasterTypeLayer;
import de.teamlapen.vampirism.client.renderer.entities.layers.VampireEntityLayer;
import de.teamlapen.vampirism.client.renderer.entities.state.TaskMasterRenderState;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireTaskMasterEntity;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Render the advanced vampire with overlays
 */
public class VampireTaskMasterRenderer extends MobRenderer<VampireTaskMasterEntity, VampireTaskMasterRenderer.VampireTaskMasterRenderState, VillagerModel> {
    private final static Identifier texture = VIdentifier.mc("textures/entity/villager/villager.png");
    private final static Identifier vampireOverlay = VIdentifier.mod("textures/entity/overlay/converted_villager.png");
    private final static Identifier overlay = VIdentifier.mod("textures/entity/vampire_task_master_overlay.png");

    public VampireTaskMasterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel(context.bakeLayer(ModEntitiesRender.TASK_MASTER)), 0.5F);
        this.addLayer(new VampireEntityLayer<>(this, vampireOverlay));
        this.addLayer(new TaskMasterTypeLayer<>(this, overlay));
    }

    @Override
    public VampireTaskMasterRenderState createRenderState() {
        return new VampireTaskMasterRenderState();
    }

    @Override
    public Identifier getTextureLocation(VampireTaskMasterRenderState entity) {
        return texture;
    }

    @Override
    protected void submitNameDisplay(VampireTaskMasterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.distanceToCameraSq <= 256) {
            super.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
        }
    }

    @Override
    public void extractRenderState(VampireTaskMasterEntity entity, VampireTaskMasterRenderer.VampireTaskMasterRenderState renderState, float partialTicks) {
        super.extractRenderState(entity, renderState, partialTicks);
        renderState.villagerData = entity.getVillageData();
    }

    @Override
    protected boolean shouldShowName(VampireTaskMasterEntity entity, double p_364446_) {
        return Helper.isVampire(entity) && super.shouldShowName(entity, p_364446_);
    }

    public static class VampireTaskMasterRenderState extends TaskMasterRenderState {
    }
}
