package de.teamlapen.vampirism.coremod;

import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

public class CoreHandler {
	public static void addExhaustion(float a,EntityPlayer p){
		VampirePlayer.get(p).getBloodStats().addExhaustion(a);
	}
}
