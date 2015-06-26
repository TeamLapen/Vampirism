package de.teamlapen.vampirism.coremod;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

/**
 * Class for static communication between modified/transformed classes and the mod
 * 
 * @author Maxanier
 */
public class CoreHandler {
	public static void addExhaustion(float a, EntityPlayer p) {
		VampirePlayer pl = VampirePlayer.get(p);
		if (pl.getLevel() > 0) {
			pl.getBloodStats().addExhaustion(a);
		}
	}

	public static ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc) {
		return VampirismMod.proxy.checkVampireTexture(entity, loc);
	}

	public static PotionEffect getFakeNightVisionEffect() {
		PotionEffect p = new PotionEffect(Potion.nightVision.id, 10000);
		p.setCurativeItems(new ArrayList<ItemStack>());
		VampirismMod.proxy.enableMaxPotionDuration(p);
		return p;
	}

	public static float getNightVisionLevel(EntityPlayer p) {
		return BALANCE.getVampirePlayerNightVision(VampirePlayer.get(p).getLevel());
	}

	public static boolean shouldOverrideNightVision(Object o, Potion p) {

		if (o instanceof EntityPlayer && p.equals(Potion.nightVision)) {
			return (VampirePlayer.get((EntityPlayer) o).getVision() == 1 && getNightVisionLevel((EntityPlayer) o) > 0F);
		}
		return false;
	}

	public static boolean shouldWakePlayer(EntityPlayer p, boolean p1, boolean p2, boolean p3) {
		if (p.worldObj.isRemote)
			return true;
		if (VampirePlayer.get(p) != null && VampirePlayer.get(p).sleepingCoffin) {
			return false;
		}
		// Logger.d("CoreHandler", String.format("Player will be woken up, remote=%s", p.worldObj.isRemote));
		return true;
	}
}
