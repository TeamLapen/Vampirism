package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.common.world.blockentity.VelmorraPortalBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.VelmorraPortalBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class VelmorraPortalRenderer<T extends VelmorraPortalBlockEntity> implements BlockEntityRenderer<T, VelmorraPortalRenderer.VelmorraPortalRenderState> {

    public static final Identifier PORTAL_LOCATION = VIdentifier.mod("textures/entity/velmorra_portal.png");

    private static final float PERC = 0.5625f;
    private static final float OFFSET = 1-PERC;

    public VelmorraPortalRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public VelmorraPortalRenderState createRenderState() {
        return new VelmorraPortalRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, VelmorraPortalRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.axis = blockEntity.getBlockState().getValue(VelmorraPortalBlock.AXIS);
    }

    @Override
    public void submit(VelmorraPortalRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        nodeCollector.submitCustomGeometry(poseStack, renderType(), (pose, consumer) -> renderCube(renderState.axis, pose.pose(), consumer));
    }


    private void renderCube(Direction.Axis axis, Matrix4f pose, VertexConsumer consumer) {
        switch (axis) {
            case X -> {
                this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, PERC, PERC, PERC, PERC, Direction.SOUTH);
                this.renderFace(pose, consumer, 1.0F, 0.0F, 0.0F, 1.0F, PERC, PERC, PERC, PERC, Direction.SOUTH);

                this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, OFFSET, OFFSET, OFFSET, OFFSET, Direction.NORTH);
                this.renderFace(pose, consumer, 1.0F, 0.0F, 1.0F, 0.0F, OFFSET, OFFSET, OFFSET, OFFSET, Direction.NORTH);

                this.renderFace(pose, consumer, 1, 1, 0, 1, PERC, OFFSET, OFFSET, PERC, Direction.EAST);
                this.renderFace(pose, consumer, 1, 1, 0, 1, OFFSET, PERC, PERC, OFFSET, Direction.EAST);

                this.renderFace(pose, consumer, 0, 0, 0F, 1, OFFSET, PERC, PERC, OFFSET, Direction.WEST);
                this.renderFace(pose, consumer, 0, 0, 0F, 1, PERC, OFFSET, OFFSET, PERC, Direction.WEST);

                this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 1.0F,  PERC, PERC,OFFSET, OFFSET, Direction.UP);
                this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 1.0F,  OFFSET, OFFSET,PERC, PERC, Direction.UP);

                this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, OFFSET, OFFSET, PERC, PERC, Direction.DOWN);
                this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, PERC, PERC, OFFSET, OFFSET, Direction.DOWN);
            }
            case Z -> {

                this.renderFace(pose, consumer, PERC, PERC, 0, 1, 1, 0, 0, 1, Direction.EAST);
                this.renderFace(pose, consumer, PERC, PERC, 0, 1, 0, 1, 1, 0, Direction.EAST);

                this.renderFace(pose, consumer, OFFSET, OFFSET, 0, 1, 0, 1, 1, 0, Direction.WEST);
                this.renderFace(pose, consumer, OFFSET, OFFSET, 0, 1, 1, 0, 0, 1, Direction.WEST);

                this.renderFace(pose, consumer, PERC, OFFSET, 1.0F, 1.0F, 0, 0, 1, 1, Direction.UP);
                this.renderFace(pose, consumer, OFFSET, PERC, 1.0F, 1.0F, 0, 0, 1, 1, Direction.UP);

                this.renderFace(pose, consumer, PERC, OFFSET, 0.0F, 0.0F, 1, 1, 0, 0, Direction.DOWN);
                this.renderFace(pose, consumer, OFFSET, PERC, 0.0F, 0.0F, 1, 1, 0, 0, Direction.DOWN);
            }
        }
    }

    private void renderFace(
            Matrix4f pose,
            VertexConsumer consumer,
            float x0,
            float x1,
            float y0,
            float y1,
            float z0,
            float z1,
            float z2,
            float z3,
            Direction direction
    ) {
        consumer.addVertex(pose, x0, y0, z0);
        consumer.addVertex(pose, x1, y0, z1);
        consumer.addVertex(pose, x1, y1, z2);
        consumer.addVertex(pose, x0, y1, z3);
    }

    protected RenderType renderType() {
        return ModRenderPipelines.VELMORRA_PORTAL_RENDER_TYPE;
    }

    public static class VelmorraPortalRenderState extends BlockEntityRenderState {
        public Direction.Axis axis = Direction.Axis.X;
    }
}
