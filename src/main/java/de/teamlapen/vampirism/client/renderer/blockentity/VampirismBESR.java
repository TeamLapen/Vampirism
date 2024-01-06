package de.teamlapen.vampirism.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TESR with a few util methods
 */
abstract class VampirismBESR<T extends BlockEntity> implements BlockEntityRenderer<T> {

    /**
     * Rotates the block to fit the enum facing.
     * ONLY CALL THIS IF THE BLOCK HAS A {@link HorizontalDirectionalBlock#FACING} PROPERTY
     */
    protected void adjustRotatePivotViaState(@Nullable BlockEntity tile, @NotNull PoseStack matrixStack) {
        if (tile == null) return;
        Direction dir = Direction.NORTH;
        if (tile.getLevel() != null) {
            dir = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING);
        }
        matrixStack.mulPose(Axis.YP.rotationDegrees((dir.get2DDataValue() - 2) * -90));
    }
}
