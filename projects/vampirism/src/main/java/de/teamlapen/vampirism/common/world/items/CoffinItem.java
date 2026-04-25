package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class CoffinItem extends BlockItem {

    public CoffinItem(CoffinBlock block, Properties properties) {
        super(block, properties);
    }

    public DyeColor getColor() {
        return ((CoffinBlock)this.getBlock()).getColor();
    }
}
