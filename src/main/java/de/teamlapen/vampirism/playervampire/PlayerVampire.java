package de.teamlapen.vampirism.playervampire;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerVampire {

	public static void makeVampire(EntityPlayer p){
		NBTTagCompound nbt =p.getEntityData();
		nbt.setBoolean("vampire", true);
		nbt.setInteger(REFERENCE.VAMPIRE_LEVEL_NBT_KEY, 1);
	}
	
	public static int getVampireLevel(EntityPlayer p){
		return p.getEntityData().getInteger(REFERENCE.VAMPIRE_LEVEL_NBT_KEY);
	}
}
