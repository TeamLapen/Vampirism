package de.teamlapen.vampirism.coremod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.PlayerAbilities;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.network.UpdateVampirePlayerPacket;
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
	
	public static PotionEffect getFakeNightVisionEffect(){
		PotionEffect p=new PotionEffect(Potion.nightVision.id,10000);
		p.setCurativeItems(new ArrayList<ItemStack>());
		VampirismMod.proxy.enableMaxPotionDuration(p);
		return p;		
	}
	
	public static ResourceLocation checkVampireTexture(Entity entity,ResourceLocation loc){
		return VampirismMod.proxy.checkVampireTexture(entity, loc);
	}
	
	public static boolean shouldWakePlayer(EntityPlayer p,boolean p1,boolean p2,boolean p3){
		//Logger.i("CoreHandler", String.format("Attempt to wake up player, remote=%s", p.worldObj.isRemote));
		if(p.worldObj.isRemote) {
			//TODO Wake player up on server
			//Logger.i("CoreHandler", "Wakeup on client called, sending UpdateVampirePacket to server now");
			//VampirePlayer.get(p).sleepingCoffin = false;
			//VampirismMod.modChannel.sendToServer(new UpdateEntityPacket(VampirePlayer.get(p)));
		}
		if(VampirePlayer.get(p) != null && VampirePlayer.get(p).sleepingCoffin) {
			return false;
		}
		Logger.i("CoreHandler", String.format("Player will be woken up, remote=%s", p.worldObj.isRemote));
		return true;
	}
}
