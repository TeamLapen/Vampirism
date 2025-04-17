package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.items.component.BottleBlood;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DarkStoneBlock extends Block {

    public DarkStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.is(ModBlocks.BLOODY_DARK_STONE_BRICKS)) {
            if (random.nextInt(180) == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.BLOOD_DRIP.get(), SoundSource.AMBIENT, 0.85F, random.nextInt(70, 100) / 100f, false);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.is(ModBlocks.DARK_STONE_BRICKS) && stack.get(ModDataComponents.BOTTLE_BLOOD) != null) {
            int blood = stack.getOrDefault(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(0)).blood();

            if (blood >= 3) {
                level.setBlockAndUpdate(pos, ModBlocks.BLOODY_DARK_STONE_BRICKS.get().defaultBlockState());
                level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 4.0F, 1.0F);

                if (!player.hasInfiniteMaterials()) {
                    stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(blood - 3));
                }

                return InteractionResult.SUCCESS_SERVER;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
