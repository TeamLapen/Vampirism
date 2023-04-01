package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CursedGrass extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    public CursedGrass(@NotNull Properties properties) {
        super(properties);
    }

    /**
     * copied from {@link net.minecraft.world.level.block.SpreadingSnowyDirtBlock#randomTick(net.minecraft.world.level.block.state.BlockState, net.minecraft.server.level.ServerLevel, net.minecraft.core.BlockPos, net.minecraft.util.RandomSource)} changing dirt to cursed earth
     */
    @Override
    public void randomTick(@NotNull BlockState p_222508_, @NotNull ServerLevel p_222509_, @NotNull BlockPos p_222510_, @NotNull RandomSource p_222511_) {
        if (!canBeGrass(p_222508_, p_222509_, p_222510_)) {
            if (!p_222509_.isAreaLoaded(p_222510_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            p_222509_.setBlockAndUpdate(p_222510_, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        } else {
            if (!p_222509_.isAreaLoaded(p_222510_, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            if (p_222509_.getMaxLocalRawBrightness(p_222510_.above()) >= 9) {
                BlockState blockstate = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = p_222510_.offset(p_222511_.nextInt(3) - 1, p_222511_.nextInt(5) - 3, p_222511_.nextInt(3) - 1);
                    if (p_222509_.getBlockState(blockpos).is(ModBlocks.CURSED_EARTH.get()) && canPropagate(blockstate, p_222509_, blockpos)) {
                        p_222509_.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, p_222509_.getBlockState(blockpos.above()).is(Blocks.SNOW)));
                    }
                }
            }

        }
    }

    @Override
    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull IPlantable plantable) {
        return plantable.getPlantType(world, pos) == PlantType.PLAINS || plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pPos, @NotNull BlockState pState, boolean pIsClient) {
        return true;
    }
    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    /**
     * copied from {@link net.minecraft.world.level.block.GrassBlock#performBonemeal(net.minecraft.server.level.ServerLevel, net.minecraft.util.RandomSource, net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState)}
     * and changed from grass to cursed grass
     */
    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockPos blockpos = pos.above();
        // changed from grass to cursed grass
        BlockState blockstate = ModBlocks.CURSED_GRASS.get().defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> optional = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);


        label46:
        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;

            for (int j = 0; j < i / 16; ++j) {
                blockpos1 = blockpos1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!level.getBlockState(blockpos1.below()).is(this) || level.getBlockState(blockpos1).isCollisionShapeFullBlock(level, blockpos1)) {
                    continue label46;
                }
            }

            BlockState blockstate1 = level.getBlockState(blockpos1);
            if (blockstate1.is(blockstate.getBlock()) && random.nextInt(10) == 0) {
                ((BonemealableBlock) blockstate.getBlock()).performBonemeal(level, random, blockpos1, blockstate1);
            }

            if (blockstate1.isAir()) {
                Holder<PlacedFeature> holder;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> list = level.getBiome(blockpos1).value().getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) {
                        continue;
                    }

                    holder = ((RandomPatchConfiguration) list.get(0).config()).feature();
                } else {
                    if (optional.isEmpty()) {
                        continue;
                    }

                    holder = optional.get();
                }

                holder.value().place(level, level.getChunkSource().getGenerator(), random, blockpos1);
            }
        }
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof HolyWaterBottleItem) {
            int uses = heldItem == ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get() ? 100 : (heldItem == ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() ? 50 : 25);
            if (player.getRandom().nextInt(uses) == 0) {
                heldItemStack.setCount(heldItemStack.getCount() - 1);
            }
            worldIn.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
