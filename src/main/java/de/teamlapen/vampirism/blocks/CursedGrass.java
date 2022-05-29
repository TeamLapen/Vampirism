package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class CursedGrass extends SpreadableSnowyDirtBlock implements IGrowable {

    public CursedGrass(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source) {
        //world.setBlock(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState(), 2);
    }

    /**
     *
     * copied from {@link SpreadableSnowyDirtBlock#randomTick(BlockState, ServerWorld, BlockPos, Random)} changing dirt to cursed earth
     */
    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        if (!canBeGrass(state, level, pos)) {
            if (!level.isAreaLoaded(pos, 3)) {
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            }
            level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        } else {
            if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                BlockState blockstate = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (level.getBlockState(blockpos).is(ModBlocks.CURSED_EARTH.get()) && canPropagate(blockstate, level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, level.getBlockState(blockpos.above()).is(Blocks.SNOW)));
                    }
                }
            }
        }
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull IPlantable plantable) {
        return plantable.getPlantType(world, pos) == PlantType.PLAINS || plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull IBlockReader blockReader, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState, boolean b) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return true;
    }

    /**
     * copied and renamed from {@link net.minecraft.block.GrassBlock#performBonemeal(ServerWorld, Random, BlockPos, BlockState)}
     */
    @Override
    public void performBonemeal(@Nonnull ServerWorld serverWorld, @Nonnull Random random, @Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        BlockPos targetPos = blockPos.above();
        BlockState grassState = this.defaultBlockState(); // only the state is changed

        label48:
        for (int i = 0; i < 128; ++i) {
            BlockPos currentPos = targetPos;

            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!serverWorld.getBlockState(currentPos.below()).is(this) || serverWorld.getBlockState(currentPos).isCollisionShapeFullBlock(serverWorld, currentPos)) {
                    continue label48;
                }
            }

            BlockState currentState = serverWorld.getBlockState(currentPos);
            if (currentState.is(grassState.getBlock()) && random.nextInt(10) == 0) {
                ((IGrowable) grassState.getBlock()).performBonemeal(serverWorld, random, currentPos, currentState);
            }

            if (currentState.isAir()) {
                BlockState flowerState;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> flowerFeatures = serverWorld.getBiome(currentPos).getGenerationSettings().getFlowerFeatures();
                    if (flowerFeatures.isEmpty()) {
                        continue;
                    }

                    ConfiguredFeature<?, ?> configuredFlowerFeature = flowerFeatures.get(0);
                    FlowersFeature flowersFeature = (FlowersFeature) configuredFlowerFeature.feature;
                    flowerState = flowersFeature.getRandomFlower(random, currentPos, configuredFlowerFeature.config());
                } else {
                    flowerState = grassState;
                }

                if (flowerState.canSurvive(serverWorld, currentPos)) {
                    serverWorld.setBlock(currentPos, flowerState, 3);
                }
            }
        }
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof HolyWaterBottleItem) {
            int uses = heldItem == ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get() ? 100 : (heldItem == ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() ? 50 : 25);
            if (player.getRandom().nextInt(uses) == 0) {
                heldItemStack.setCount(heldItemStack.getCount() - 1);
            }
            worldIn.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            return ActionResultType.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
