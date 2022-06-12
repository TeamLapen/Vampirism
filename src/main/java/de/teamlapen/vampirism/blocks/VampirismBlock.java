package de.teamlapen.vampirism.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Vampirism default block with set creative tab, registry name and unloc name
 */
public class VampirismBlock extends Block {

    private boolean isDecorativeBlock;

    public VampirismBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack p_190948_1_, @Nullable BlockGetter p_190948_2_, @Nonnull List<Component> p_190948_3_, @Nonnull TooltipFlag p_190948_4_) {
        super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
        if (isDecorativeBlock) {
            p_190948_3_.add(new TranslatableComponent("text.vampirism.decorative_only").withStyle(ChatFormatting.GRAY));
        }
    }

    public VampirismBlock markDecorativeBlock() {
        this.isDecorativeBlock = true;
        return this;
    }
}
