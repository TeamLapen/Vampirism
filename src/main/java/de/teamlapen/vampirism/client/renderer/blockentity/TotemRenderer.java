package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class TotemRenderer implements BlockEntityRenderer<TotemBlockEntity, TotemRenderer.TotemRenderState> {

    private static final ResourceLocation TOTEM_BEAM_LOCATION = VResourceLocation.mod("textures/entity/totem_beam.png");
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
        renderState.faction = blockEntity.getControllingFaction();

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
        } else {
//            if (!IFaction.isNeutral(renderState.faction)) {
//                renderFactionName(renderState.faction.value(), poseStack, bufferSource, packedLight);
//            }
        }
    }

    private void renderFactionName(IFaction<?> faction, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!ModConfig.CLIENT.renderTotemFactionName.getAsBoolean()) return;
        Component displayNameIn = faction.getNamePlural().plainCopy().withStyle(style -> style.withColor((faction.getChatColor())));
        poseStack.pushPose();
        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        poseStack.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float nameOffset = (float) (-font.width(displayNameIn) / 2);
        font.drawInBatch(displayNameIn, nameOffset, 0, -2130706433, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(displayNameIn, nameOffset, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
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
        public Holder<? extends IFaction<?>> faction;
    }
}
