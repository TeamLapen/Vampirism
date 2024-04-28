package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.CoffinBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Render the coffin with its different colors and the lid opening animation
 */
public class CoffinBESR extends VampirismBESR<CoffinBlockEntity> {
    private static final Marker COFFIN = new MarkerManager.Log4jMarker("COFFIN");
    private final Logger LOGGER = LogManager.getLogger();

    public CoffinBESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull CoffinBlockEntity tile, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource iRenderTypeBuffer, int i, int i1) {
        BlockState state = tile.getBlockState();
        Direction direction = state.getValue(HORIZONTAL_FACING);

        if (!isHeadSafe(tile.getLevel(), tile.getBlockPos())) return;
        Material material = ModBlocksRender.COFFIN_TEXTURES[tile.color.getId()];
        matrixStack.pushPose();
        boolean vertical = state.getValue(CoffinBlock.VERTICAL);
        switch (direction) {
            case EAST -> {
                if (vertical) {
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
                    matrixStack.translate(0, -1, 0);
                }
                matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                matrixStack.translate(-1, 0, -1);
            }
            case WEST -> {
                if (vertical) {
                    matrixStack.mulPose(Axis.ZN.rotationDegrees(90));
                    matrixStack.translate(-1, 0, 0);
                }
                matrixStack.mulPose(Axis.YN.rotationDegrees(90));
                matrixStack.translate(0, 0, -2);
            }
            case SOUTH -> {
                if (vertical) {
                    matrixStack.mulPose(Axis.XN.rotationDegrees(90));
                    matrixStack.translate(0, -1, 0);
                }
                matrixStack.translate(0, 0, -1);
            }
            case NORTH -> {
                if (vertical) {
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90));
                    matrixStack.translate(0, 0, -1);
                }
                matrixStack.mulPose(Axis.YP.rotationDegrees(180));
                matrixStack.translate(-1, 0, -2);
            }
        }

        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_bottom_" + tile.color.getName()));
        ModelData modelData = baseModel.getModelData(tile.getLevel(), tile.getBlockPos(), state, ModelData.EMPTY);
        for (RenderType renderType : baseModel.getRenderTypes(state, RandomSource.create(42), modelData)) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), material.buffer(iRenderTypeBuffer, RenderType::entitySolid), state, baseModel, 1, 1, 1, i, i1, modelData, renderType);
        }

        matrixStack.pushPose();
        if (vertical) {
            matrixStack.mulPose(Axis.ZP.rotationDegrees(80 * tile.lidPos));
            matrixStack.translate(0, -0.5 * tile.lidPos, 0);
        } else {
            matrixStack.mulPose(Axis.YN.rotationDegrees(35 * tile.lidPos));
            matrixStack.translate(0, 0, -0.5 * tile.lidPos);
        }

        BakedModel lidModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_top_" + tile.color.getName()));
        modelData = lidModel.getModelData(tile.getLevel(), tile.getBlockPos(), state, ModelData.EMPTY);
        for (RenderType renderType : lidModel.getRenderTypes(state, RandomSource.create(42), modelData)) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), iRenderTypeBuffer.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false)), state, lidModel, 1, 1, 1, i, i1, modelData, renderType);
        }
        matrixStack.popPose();
        matrixStack.popPose();
    }

    /**
     * Checks if the coffin part at the given pos is the head of the coffin. Any exception is caught and false is returned
     */
    private boolean isHeadSafe(@NotNull Level world, @NotNull BlockPos pos) {
        try {
            return CoffinBlock.isHead(world, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at {} caused by wrong blockstate. Block at that pos: {}", pos, world.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at " + pos + ".", e);
        }
        return false;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(CoffinBlockEntity blockEntity) {
        BlockPos worldPosition = blockEntity.getBlockPos();
        return new AABB(worldPosition.getX() - 4, worldPosition.getY(), worldPosition.getZ() - 4, worldPosition.getX() + 4, worldPosition.getY() + 2, worldPosition.getZ() + 4);
    }
}
