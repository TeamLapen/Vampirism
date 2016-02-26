
package de.teamlapen.lib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Standard item for meta blocks. Blocks can implement {@link IMetaItemName} to provide a name for the item stack
 */
public class ItemMetaBlock extends ItemBlock {
    final boolean customName;

    public ItemMetaBlock(Block block) {
        super(block);
        customName = (block instanceof IMetaItemName);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (customName) {
            String name = ((IMetaItemName) this.block).getItemstackName(stack);
            return super.getUnlocalizedName(stack) + "." + name;
        }
        return super.getUnlocalizedName(stack);
    }

    public interface IMetaItemName {
        /**
         * Get the special name, which is added to the default unloc name (<unlocname>.<special name>)
         *
         * @param stack
         * @return
         */
        String getItemstackName(ItemStack stack);
    }
}
