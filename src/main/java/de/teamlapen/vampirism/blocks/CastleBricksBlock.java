package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CastleBricksBlock extends VampirismBlock {
    private final EnumVariant variant;

    public CastleBricksBlock(EnumVariant variant) {
        super(Properties.of().mapColor(MapColor.STONE).strength(2, 10).sound(SoundType.STONE));
        this.variant = variant;

    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (!CastleStairsBlock.isStairs(state) && variant == EnumVariant.DARK_BRICK_BLOODY) {
            if (rand.nextInt(180) == 0) {
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.AMBIENT_BLOOD_DRIPPING.get(), SoundSource.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(Component.translatable("block.vampirism.castle_block" + (variant == EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }

    public EnumVariant getVariant() {
        return variant;
    }


    public enum EnumVariant implements StringRepresentable {
        DARK_BRICK("dark_brick"),
        PURPLE_BRICK("purple_brick"),
        DARK_BRICK_BLOODY("dark_brick_bloody"),
        NORMAL_BRICK("normal_brick"),
        DARK_STONE("dark_stone"),
        CRACKED_DARK_BRICK("cracked_dark_brick");

        private final String name;

        EnumVariant(String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return this.getSerializedName();
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
