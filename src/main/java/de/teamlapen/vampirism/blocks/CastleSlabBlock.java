package de.teamlapen.vampirism.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class CastleSlabBlock extends SlabBlock {
    private final CastleBricksBlock.EnumVariant variant;

    public CastleSlabBlock(CastleBricksBlock.EnumVariant variant) {
        super(Properties.of(Material.STONE).strength(2, 10).sound(SoundType.STONE));
        this.variant = variant;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter player, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, player, tooltip, advanced);
        tooltip.add(Component.translatable("block.vampirism.castle_block" + (variant == CastleBricksBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

    }

    public CastleBricksBlock.EnumVariant getVariant() {
        return variant;
    }
}
