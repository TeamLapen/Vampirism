package de.teamlapen.vampirism.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Vampirism default block with set creative tab, registry name and unloc name
 */
public class VampirismBlock extends Block {

    private boolean isDecorativeBlock;

    public VampirismBlock(Block.@NotNull Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_190948_1_, Item.TooltipContext p_190948_2_, @NotNull List<Component> p_190948_3_, @NotNull TooltipFlag p_190948_4_) {
        super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
        if (isDecorativeBlock) {
            p_190948_3_.add(Component.translatable("text.vampirism.decorative_only").withStyle(ChatFormatting.GRAY));
        }
    }

    public @NotNull VampirismBlock markDecorativeBlock() {
        this.isDecorativeBlock = true;
        return this;
    }
}
