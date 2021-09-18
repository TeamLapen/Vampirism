package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import java.util.Random;

public class CursedEarthBlock extends VampirismBlock implements BonemealableBlock {

    private static final String name = "cursed_earth";

    public CursedEarthBlock() {
        super(name, Properties.of(Material.DIRT).strength(0.5f, 2.0f).sound(SoundType.GRAVEL));

    }

    @Override
    public boolean isBonemealSuccess(@Nonnull Level worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull IPlantable plantable) {
        return plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public boolean isValidBonemealTarget(@Nonnull BlockGetter iBlockReader, @Nonnull BlockPos blockPos, @Nonnull BlockState iBlockState, boolean b) {
        return true;
    }

    @Override
    public void performBonemeal(@Nonnull ServerLevel worldIn, @Nonnull Random rand, BlockPos pos, @Nonnull BlockState state) {
        BlockPos blockpos = pos.above();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isEmptyBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            VampirismFlowerBlock blockflower = ModBlocks.vampire_orchid;
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

                if (worldIn.getBlockState(blockpos1.below()).getBlock() != ModBlocks.cursed_earth || worldIn.getBlockState(blockpos1).isRedstoneConductor(worldIn, blockpos1)) {
                    break;
                }

                ++j;
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof HolyWaterBottleItem) {
            int uses = heldItem == ModItems.holy_water_bottle_ultimate ? 100 : (heldItem == ModItems.holy_water_bottle_enhanced ? 50 : 25);
            if (player.getRandom().nextInt(uses) == 0) {
                heldItemStack.setCount(heldItemStack.getCount() - 1);
            }
            worldIn.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
