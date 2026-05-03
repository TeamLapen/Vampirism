package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.client.core.ModModels;
import de.teamlapen.vampirism.common.world.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.renderer.block.BlockModelRenderState.EMPTY_TINTS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Render the coffin with its different colors and the lid opening animation
 */
public class CoffinRenderer implements BlockEntityRenderer<CoffinBlockEntity, CoffinRenderer.CoffinRenderState> {
    
    private static final Marker COFFIN = new MarkerManager.Log4jMarker("COFFIN");
    private final Logger LOGGER = LogManager.getLogger();

    private final BlockStateModelPart[] bottom;
    private final BlockStateModelPart top;

    public CoffinRenderer(BlockEntityRendererProvider.Context context) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        bottom = ModModels.COFFIN_BOTTOM_KEYS.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getId()))
                .map(Map.Entry::getValue)
                .map(modelManager::getStandaloneModel)
                .toArray(BlockStateModelPart[]::new);
        top = modelManager.getStandaloneModel(ModModels.COFFIN_TOP_KEY);
    }

    @Override
    public CoffinRenderState createRenderState() {
        return new CoffinRenderState();
    }

    @Override
    public void extractRenderState(CoffinBlockEntity blockEntity, CoffinRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.color = blockEntity.color;
        renderState.isVertical = blockEntity.getBlockState().getValue(CoffinBlock.VERTICAL);
        renderState.facing = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);
        renderState.lidPos = blockEntity.lidPos;
    }

    @Override
    public void submit(CoffinRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {

        poseStack.pushPose();
        switch (renderState.facing) {
            case EAST -> {
                if (renderState.isVertical) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    poseStack.translate(0, -1, 0);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(-1, 0, -1);
            }
            case WEST -> {
                if (renderState.isVertical) {
                    poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    poseStack.translate(-1, 0, 0);
                }
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
                poseStack.translate(0, 0, -2);
            }
            case SOUTH -> {
                if (renderState.isVertical) {
                    poseStack.mulPose(Axis.XN.rotationDegrees(90));
                    poseStack.translate(0, -1, 0);
                }
                poseStack.translate(0, 0, -1);
            }
            case NORTH -> {
                if (renderState.isVertical) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.translate(0, 0, -1);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(-1, 0, -2);
            }
        }

        nodeCollector.submitBlockModel(poseStack, RenderTypes.cutoutMovingBlock(), List.of(this.bottom[renderState.color.getId()]), EMPTY_TINTS, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.pushPose();
        if (renderState.isVertical) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(80 * renderState.lidPos));
            poseStack.translate(0, -0.5 * renderState.lidPos, 0);
        } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(35 * renderState.lidPos));
            poseStack.translate(0, 0, -0.5 * renderState.lidPos);
        }

        nodeCollector.submitBlockModel(poseStack, RenderTypes.solidMovingBlock(), List.of(this.top), EMPTY_TINTS, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.popPose();
    }

    /**
     * Checks if the coffin part at the given pos is the head of the coffin. Any exception is caught and false is returned
     */
    private boolean isHeadSafe(Level level, BlockPos pos) {
        try {
            return CoffinBlock.isHead(level, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at {} caused by wrong blockstate. Block at that pos: {}", pos, level.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at {}.", pos, e);
        }

        return false;
    }

    @Override
    public boolean shouldRender(CoffinBlockEntity blockEntity, Vec3 cameraPos) {
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPos) && isHeadSafe(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    @Override
    public AABB getRenderBoundingBox(CoffinBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }

    public static class CoffinRenderState extends BlockEntityRenderState {
        public DyeColor color;
        public boolean isVertical;
        public Direction facing;
        public float lidPos;
    }
}
