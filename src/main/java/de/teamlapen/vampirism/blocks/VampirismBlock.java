package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

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
    public void appendHoverText(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
        super.appendHoverText(p_190948_1_, p_190948_2_, p_190948_3_, p_190948_4_);
        if (isDecorativeBlock) {
            p_190948_3_.add(new TranslationTextComponent("text.vampirism.decorative_only").withStyle(TextFormatting.GRAY));
        }
    }

    public VampirismBlock markDecorativeBlock() {
        this.isDecorativeBlock = true;
        return this;
    }
}
