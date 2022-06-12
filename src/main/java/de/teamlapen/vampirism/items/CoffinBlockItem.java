package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class CoffinBlockItem extends BlockItem {

    public CoffinBlockItem(CoffinBlock block) {
        super(block, new Item.Properties().tab(VampirismMod.creativeTab));
    }
}
