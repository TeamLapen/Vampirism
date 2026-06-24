package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class SpitfireBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    @Override
    public int color() {
        return 0xFFFF2211;
    }

    @Override
    public void onHitBlock(ItemStack arrow, BlockPos blockPos, AbstractArrow arrowEntity, @Nullable Entity shootingEntity, Direction direction) {
        createAlchemicalFireSplash(arrowEntity.level(), blockPos, direction, 2.5, 5);
    }

    /**
     * Creates a circular splash of alchemical fire centered on the hit block. Every block within the radius has a
     * chance to be set alight that is higher towards the center, and any campfires, candles or candle cakes the splash
     * covers are lit instead. Does nothing if the hit block was reached through a fluid (e.g. an arrow shot into
     * water and lodged in the floor), so submerged hits never ignite anything.
     *
     * @param pos the hit block, used as the center of the splash. Not the air block, the block that was hit
     * @param direction the direction from which the hit came. Therefore, if an arrow hit the block
     *                  from the top, the direction should be up
     * @param radius the radius of the fire splash
     * @param surfaceDepth the depth that the alchemical fire can reach. Blocks below + center + blocks above
     */
    public static void createAlchemicalFireSplash(Level level, BlockPos pos, Direction direction, double radius, int surfaceDepth) {
        RandomSource random = level.getRandom();
        int ceilRadius = (int) Math.ceil(radius);

        if (!level.getBlockState(pos.relative(direction)).getFluidState().isEmpty()) return;

        for (int dx = -ceilRadius; dx <= ceilRadius; dx++) {
            for (int dz = -ceilRadius; dz <= ceilRadius; dz++) {
                BlockPos targetPos = pos.offset(dx, 0, dz);

                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > radius) continue;

                double relative = distance / radius;
                double chance = Math.pow(2, -relative * relative * 9);

                if (random.nextDouble() > chance) continue;

                placeFire(level, targetPos, surfaceDepth);
            }
        }
    }

    private static void placeFire(Level level, BlockPos pos, int surfaceDepth) {
        for (int n = 0; n <= surfaceDepth; n++) {
            BlockPos basePos = pos.above(getHeightSearchOffset(n));
            BlockPos targetPos = basePos.above();
            BlockState baseState = level.getBlockState(basePos);
            BlockState targetState = level.getBlockState(targetPos);

            if (!targetState.getFluidState().isEmpty()) return;

            if (!targetState.canBeReplaced() || baseState.canBeReplaced()) {
                continue;
            }

            if (CampfireBlock.canLight(baseState) || CandleBlock.canLight(baseState) || CandleCakeBlock.canLight(baseState)) {
                level.setBlock(basePos, baseState.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
                return;
            }

            BlockState fireState = BaseFireBlock.getState(level, targetPos);
            if (fireState.getBlock() instanceof BaseFireBlock) {
                fireState = ModBlocks.ALCHEMICAL_FIRE.get().defaultBlockState();
            }
            level.setBlock(targetPos, fireState, Block.UPDATE_ALL_IMMEDIATE);
            return;
        }
    }

    private static int getHeightSearchOffset(int n) {
        return Math.powExact(-1, n + 1) * (int) Math.ceil(n * 0.5); // 0, 1, -1, 2, -2 and so on
    }

    @Override
    public Component getEffectDescription() {
        return Component.translatable("tooltip.vampirism.quarrel_spitfire");
    }

    @Override
    public float baseDamage(Level level, ItemStack stack, @Nullable LivingEntity shooter) {
        return 0.5f;
    }

    @Override
    public void modifyArrow(Level level, ItemStack stack, @Nullable LivingEntity shooter, AbstractArrow arrow) {
        arrow.setRemainingFireTicks(100);
    }
}
