package de.teamlapen.factions.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.common.world.blockentity.TotemBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TotemRenderer implements BlockEntityRenderer<TotemBlockEntity, TotemRenderer.TotemRenderState> {

    private static final Identifier TOTEM_BEAM_LOCATION = FResourceLocation.mod("textures/entity/totem_beam.png");
    private final static int HEIGHT = 100;

    public TotemRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void extractRenderState(TotemBlockEntity blockEntity, TotemRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.textureScale = blockEntity.shouldRenderBeam();
        renderState.partialTicks = partialTick;
        renderState.captureColor = blockEntity.getCapturingColors();
        renderState.baseColor = blockEntity.getBaseColor();
        renderState.capturingProgress = blockEntity.getCaptureProgress();
        Holder<? extends IFaction<?>> controllingFaction = blockEntity.getControllingFaction();
        if (!IFaction.isNeutral(controllingFaction)) {
            renderState.factionName = controllingFaction.value().getNamePlural().withStyle(style -> style.withColor(controllingFaction.value().getChatColor()));
        }
        renderState.distanceToCamera = blockEntity.getBlockPos().distToCenterSqr(cameraPosition);

    }

    @Override
    public TotemRenderState createRenderState() {
        return new TotemRenderState();
    }

    @Override
    public void submit(TotemRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.textureScale > 0.0f) {
            int captureProgress = renderState.capturingProgress;
            int baseColors = renderState.baseColor;
            int offset = 0;
            if (captureProgress > 0) {
                int color = renderState.captureColor;
                offset = (captureProgress * HEIGHT) / 100;
                BeaconRenderer.submitBeaconBeam(poseStack, nodeCollector, TOTEM_BEAM_LOCATION, renderState.partialTicks, renderState.time, 0, offset, color, 0.2f, 0.25f);
            }
            BeaconRenderer.submitBeaconBeam(poseStack, nodeCollector, TOTEM_BEAM_LOCATION, renderState.partialTicks, renderState.time, offset, HEIGHT - offset, baseColors, 0.2f, 0.25f);
        } else if (renderState.factionName != null) {
            nodeCollector.submitNameTag(poseStack, new Vec3(0.5f,0.5,0.5f), 1, renderState.factionName, true, renderState.lightCoords, renderState.distanceToCamera, cameraRenderState);
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TotemBlockEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class TotemRenderState extends BlockEntityRenderState {
        public float textureScale;
        public int time;
        public float partialTicks;
        public int captureColor;
        public int baseColor;
        public int capturingProgress;
        public @Nullable Component factionName;
        public double distanceToCamera;
    }
}
