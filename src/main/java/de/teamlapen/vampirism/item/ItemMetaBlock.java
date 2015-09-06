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
		return super.getUnlocalizedName(stack) + "." + ((IMetaBlockName)this.block).getSpecialName(stack);
	}

	public static interface IMetaBlockName{
		String getSpecialName(ItemStack stack);
	}
}
