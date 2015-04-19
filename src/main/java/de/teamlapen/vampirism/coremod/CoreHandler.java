package de.teamlapen.vampirism.coremod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.PlayerAbilities;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

/**
 * Class for static communication between modified/transformed classes and the
 * mod
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

	public static float getNightVisionLevel(EntityPlayer p) {
		return PlayerAbilities.getPlayerAbilities(VampirePlayer.get(p).getLevel()).nightVision;
	}

	public static boolean shouldOverrideNightVision(Object o, Potion p) {

		if (o instanceof EntityPlayer && p.equals(Potion.nightVision)) {
			return (PlayerAbilities.getPlayerAbilities(VampirePlayer.get((EntityPlayer) o).getLevel()).nightVision > 0.0F);
		}
		return false;
	}
	
	public static ResourceLocation checkVampireTexture(Entity entity,ResourceLocation loc){
		return VampirismMod.proxy.checkVampireTexture(entity, loc);
	}
	
	public static boolean shouldWakePlayer(EntityPlayer p){
		//Logger.i("tes", "adsf");
		if(VampirePlayer.get(p) != null && VampirePlayer.get(p).sleepingCoffin) {
			return false;
		}
		Logger.i("CoreHandler", String.format("sleepingCoffin=%s, remote=foo", VampirePlayer.get(p).sleepingCoffin));
		return true;
	}
}
