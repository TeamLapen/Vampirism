package de.teamlapen.vampirism.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;


public class ItemMetaBlock extends ItemBlock {
	public ItemMetaBlock(Block p1) {
		super(p1);
		if(!(p1 instanceof IMetaBlockName)){
			throw new IllegalArgumentException(String.format("The given block %s does not implement IMetaBlockName",p1));
		}
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int damage){
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name=((IMetaBlockName)this.block).getSpecialName(stack);
		return super.getUnlocalizedName(stack) +((name==null)?"": ("." +name)) ;
	}

	public static interface IMetaBlockName{
		/**
		 * Get the special name, which is added to the default unloc name (<unlocname>.<special name>) Can be null, if no extra name should be added
		 * @param stack
		 * @return
		 */
		String getSpecialName(ItemStack stack);
	}
}
