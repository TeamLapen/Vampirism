package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import de.teamlapen.vampirism.items.component.BottleBlood;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DarkStoneBlock extends VampirismBlock {

    public DarkStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.is(ModBlocks.BLOODY_DARK_STONE_BRICKS)) {
            if (random.nextInt(180) == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.BLOOD_DRIP.get(), SoundSource.AMBIENT, 0.7F, random.nextInt(70, 100) / 100f, false);
            }
        }
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
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

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, context, tooltip, advanced);
        if (stack.is(ModItemTags.NO_SPAWN)) {
            tooltip.add(Component.translatable("block.vampirism.castle_block.no_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        } else if (stack.is(ModItemTags.VAMPIRE_SPAWN)) {
            tooltip.add(Component.translatable("block.vampirism.castle_block.vampire_spawn").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
}
