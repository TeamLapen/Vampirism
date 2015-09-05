package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemGemOfBinding extends BasicItem {

	public final static String name = "gemOfBinding";

	public ItemGemOfBinding() {
		super(name);
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if (!player.worldObj.isRemote && entity instanceof EntitySaveableVampireMinion) {
			EntitySaveableVampireMinion m = (EntitySaveableVampireMinion) entity;
			if (VampirePlayer.get(player).equals(m.getLord())) {
				m.convertToRemote();
				stack.stackSize--;
				return true;
			}
		}
		return false;
	}
}
