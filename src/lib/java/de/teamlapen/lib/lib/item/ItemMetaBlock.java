
package de.teamlapen.lib.lib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Standard item for meta blocks. Blocks can implement {@link IMetaItemName} to provide a name for the item stack
 */
public class ItemMetaBlock extends ItemBlock {
    final boolean customName;

    /**
     * Creates a item for a meta block
     *
     * @param block
     * @param register If the block's registry name should be set to the item
     */
    public ItemMetaBlock(Block block, boolean register) {
        super(block);
        customName = (block instanceof IMetaItemName);
        setHasSubtypes(true);
        if (register) this.setRegistryName(block.getRegistryName());
    }

    /**
     * Creates a item for a meta block and copies it's registry name
     *
     * @param block
     */
    public ItemMetaBlock(Block block) {
        this(block,true);
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
