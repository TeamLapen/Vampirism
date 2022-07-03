package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;


public class CursedGrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    public CursedGrassBlock() {
        super(BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_PURPLE).randomTicks().strength(0.6F).sound(SoundType.GRASS));
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull IPlantable plantable) {
        return plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public boolean isBonemealSuccess(@Nonnull Level worldIn, @Nonnull RandomSource rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull BlockGetter iBlockReader, @Nonnull BlockPos blockPos, @Nonnull BlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public void performBonemeal(@Nonnull ServerLevel worldIn, @Nonnull RandomSource rand, BlockPos pos, @Nonnull BlockState state) {
        BlockPos blockpos = pos.above();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isEmptyBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            VampirismFlowerBlock blockflower = ModBlocks.VAMPIRE_ORCHID.get();
                            BlockState iblockstate = blockflower.defaultBlockState();

                            if (blockflower.canSurvive(iblockstate, worldIn, blockpos1)) {
                                worldIn.setBlock(blockpos1, iblockstate, 3);
                            }
                        } else {
                            BlockState iblockstate1 = Blocks.TALL_GRASS.defaultBlockState();

                            if (Blocks.TALL_GRASS.canSurvive(iblockstate1, worldIn, blockpos1)) {
                                worldIn.setBlock(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.below()).getBlock() != ModBlocks.CURSED_EARTH.get() || worldIn.getBlockState(blockpos1).isRedstoneConductor(worldIn, blockpos1)) {
                    break;
                }

                ++j;
            }
        }
    }

    /**
     * Copied from {@link SpreadingSnowyDirtBlock}, replacing DIRT with cursed_eart
     */
    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeGrass(blockState, level, pos)) {
            if (!level.isAreaLoaded(pos, 3))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            level.setBlockAndUpdate(pos, ModBlocks.CURSED_EARTH.get().defaultBlockState());
        } else {
            if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                BlockState thisDefaultBlockState = this.defaultBlockState();

                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (level.getBlockState(blockpos).is(ModBlocks.CURSED_EARTH.get()) && canPropagate(thisDefaultBlockState, level, blockpos)) {
                        level.setBlockAndUpdate(blockpos, thisDefaultBlockState.setValue(SNOWY, level.getBlockState(blockpos.above()).is(Blocks.SNOW)));
                    }
                }
            }

        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
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
