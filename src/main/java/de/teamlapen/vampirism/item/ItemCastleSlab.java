package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.block.BlockCastleSlabDouble;
import de.teamlapen.vampirism.block.BlockCastleSlabHalf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;

/**
 * Created by Max on 07.09.2015.
 */
public class ItemCastleSlab extends ItemSlab {
    public ItemCastleSlab(Block block, BlockCastleSlabHalf singleSlab, BlockCastleSlabDouble doubleSlab,Boolean stacked) {
        super(block, singleSlab, doubleSlab);
    }
}
