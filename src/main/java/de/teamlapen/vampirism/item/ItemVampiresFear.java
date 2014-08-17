package de.teamlapen.vampirism.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

public class ItemVampiresFear extends ItemSword {

	public ItemVampiresFear(ToolMaterial p_i45356_1_) {
		super(p_i45356_1_);
		// TODO Auto-generated constructor stub
		this.setNoRepair();
	}

	
	
	// TODO change the following two classes
	@Override
	public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_,
			EntityLivingBase p_77644_3_) {
		p_77644_1_.damageItem(1, p_77644_3_);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack p_150894_1_, World p_150894_2_,
			Block p_150894_3_, int p_150894_4_, int p_150894_5_,
			int p_150894_6_, EntityLivingBase p_150894_7_) {
		if ((double) p_150894_3_.getBlockHardness(p_150894_2_, p_150894_4_,
				p_150894_5_, p_150894_6_) != 0.0D) {
			p_150894_1_.damageItem(2, p_150894_7_);
		}

		return true;
	}

}
