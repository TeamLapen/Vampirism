package de.teamlapen.vampirism.coremod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import de.teamlapen.vampirism.entity.player.PlayerAbilities;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

public class CoreHandler {
	public static void addExhaustion(float a,EntityPlayer p){
		VampirePlayer pl=VampirePlayer.get(p);
		if(pl.getLevel()>0){
			pl.getBloodStats().addExhaustion(a);
		}
	}
	
	public static float getNightVisionLevel(EntityPlayer p){
			return PlayerAbilities.getPlayerAbilities(VampirePlayer.get(p).getLevel()).nightVision;
	}
	
	public static boolean shouldOverrideNightVision(Object o,Potion p){
		
		if(o instanceof EntityPlayer && p.equals(Potion.nightVision)){
			return (PlayerAbilities.getPlayerAbilities(VampirePlayer.get((EntityPlayer)o).getLevel()).nightVision>0.0F);
		}
		return false;
	}
}
