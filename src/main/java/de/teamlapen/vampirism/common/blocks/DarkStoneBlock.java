package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
            boolean isCreative = player.getAbilities().instabuild;
            boolean isStacked = stack.getCount() > 1;

            ItemStack bottle = (!isCreative && isStacked) ? stack.split(1) : stack;
            int blood = bottle.getOrDefault(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(0)).blood();

            if (blood >= 1) {
                level.setBlockAndUpdate(pos, ModBlocks.BLOODY_DARK_STONE_BRICKS.get().defaultBlockState());
                level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 4.0F, 1.0F);

                if (!isCreative) {
                    int bloodLeft = blood - 1;
                    ItemStack resultBottle;

                    if (bloodLeft <= 0 && ModConfig.COMMON.autoConvertGlassBottles.get()) {
                        resultBottle = new ItemStack(Items.GLASS_BOTTLE);
                    } else {
                        resultBottle = bottle;
                        resultBottle.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(bloodLeft));
                    }

                    if (isStacked) {
                        if (!player.addItem(resultBottle)) {
                            player.drop(resultBottle, false);
                        }
                    } else if (bloodLeft <= 0 && ModConfig.COMMON.autoConvertGlassBottles.get()) {
                        player.setItemInHand(hand, resultBottle);
                    }
                }

                return InteractionResult.SUCCESS_SERVER;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
