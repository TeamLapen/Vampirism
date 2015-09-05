package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemMinionNameTag extends BasicItem {

	public final static String name = "minionNameTag";

	public ItemMinionNameTag() {
		super(name);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if (!stack.hasDisplayName()) {
			return false;
		} else if (entity instanceof EntityVampireMinion) {
			EntityVampireMinion entityliving = (EntityVampireMinion) entity;
			if (entityliving.tryToSetName(stack.getDisplayName(), player)) {
				--stack.stackSize;
			}
			return true;
		}
		return super.itemInteractionForEntity(stack, player, entity);
	}

}
