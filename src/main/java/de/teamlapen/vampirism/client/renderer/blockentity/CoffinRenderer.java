package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Render the coffin with its different colors and the lid opening animation
 */
public class CoffinRenderer implements BlockEntityRenderer<CoffinBlockEntity> {
    
    private static final Marker COFFIN = new MarkerManager.Log4jMarker("COFFIN");
    private final Logger LOGGER = LogManager.getLogger();

    public CoffinRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CoffinBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) return;
        
        BlockState state = blockEntity.getBlockState();
        Direction direction = state.getValue(HORIZONTAL_FACING);

        if (!isHeadSafe(blockEntity.getLevel(), blockEntity.getBlockPos())) return;
        
        poseStack.pushPose();
        boolean vertical = state.getValue(CoffinBlock.VERTICAL);
        switch (direction) {
            case EAST -> {
                if (vertical) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                    poseStack.translate(0, -1, 0);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(-1, 0, -1);
            }
            case WEST -> {
                if (vertical) {
                    poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    poseStack.translate(-1, 0, 0);
                }
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
                poseStack.translate(0, 0, -2);
            }
            case SOUTH -> {
                if (vertical) {
                    poseStack.mulPose(Axis.XN.rotationDegrees(90));
                    poseStack.translate(0, -1, 0);
                }
                poseStack.translate(0, 0, -1);
            }
            case NORTH -> {
                if (vertical) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.translate(0, 0, -1);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(-1, 0, -2);
            }
        }

        BakedModel baseModel = Minecraft.getInstance().getModelManager().getStandaloneModel(VResourceLocation.mod("block/coffin/coffin_bottom_" + blockEntity.color.getName()));
        ModelData modelData = baseModel.getModelData(blockEntity.getLevel(), blockEntity.getBlockPos(), state, ModelData.EMPTY);
        for (RenderType renderType : baseModel.getRenderTypes(state, RandomSource.create(42), modelData)) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(renderType)), state, baseModel, 1, 1, 1, packedLight, packedOverlay, modelData, renderType);
        }

        poseStack.pushPose();
        if (vertical) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(80 * blockEntity.lidPos));
            poseStack.translate(0, -0.5 * blockEntity.lidPos, 0);
        } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(35 * blockEntity.lidPos));
            poseStack.translate(0, 0, -0.5 * blockEntity.lidPos);
        }

        BakedModel lidModel = Minecraft.getInstance().getModelManager().getStandaloneModel(VResourceLocation.mod("block/coffin/coffin_top_" + blockEntity.color.getName()));
        modelData = lidModel.getModelData(blockEntity.getLevel(), blockEntity.getBlockPos(), state, ModelData.EMPTY);
        for (RenderType renderType : lidModel.getRenderTypes(state, RandomSource.create(42), modelData)) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(renderType)), state, lidModel, 1, 1, 1, packedLight, packedOverlay, modelData, renderType);
        }
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
    public AABB getRenderBoundingBox(CoffinBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }
}
