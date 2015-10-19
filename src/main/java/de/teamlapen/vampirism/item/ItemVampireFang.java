package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemVampireFang extends BasicItem {

	public static final String NAME = "vampireFang";

	public ItemVampireFang() {
		super(NAME);
	}

	protected ItemVampireFang(String name) {
		super(name);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.isPotionActive(ModPotion.sanguinare) && VampirePlayer.get(player).getLevel() == 0) {
			if (ModPotion.sanguinare.id < 0) {
				//This should not be able to happen, but I have seen a crash where it seems so
				Logger.e("Potion", "Potion ID of sanguinare seems to be lower than zero. This should be impossible. Skipping sanguinare effect");
				VampirePlayer.get(player).levelUp();
			} else {
				player.addPotionEffect(new PotionEffect(ModPotion.sanguinare.id, BALANCE.VAMPIRE_PLAYER_SANGUINARE_DURATION * 20));
			}
			player.addPotionEffect(new PotionEffect(Potion.poison.id, 60));
		}
		return stack;
	}

}
