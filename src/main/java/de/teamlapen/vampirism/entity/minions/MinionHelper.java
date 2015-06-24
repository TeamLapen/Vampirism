package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

public class MinionHelper {
	
	/**
	 * Tries to get the IMinion instance from the given entity
	 * @param e
	 * @return
	 */
	public static @Nullable IMinion getMinionFromEntity(Entity e){
		if(e instanceof IMinion)return (IMinion) e;
		
		if(e instanceof EntityCreature){
			VampireMob m=VampireMob.get((EntityCreature) e);
			if(m.isMinion())return m;
		}
		return null;
	}
	
	/**
	 * Checks if the given minion's lord is a player
	 * @param m Can be null
	 * @return
	 */
	public static boolean isLordPlayer(@Nullable IMinion m){
		if(m!=null&&m.getLord() instanceof VampirePlayer){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given entity is the minions lord. Contains null check.
	 * @param m
	 * @param e
	 * @return
	 */
	public static boolean isLordSafe(@NonNull IMinion m,@Nullable EntityLivingBase e){
		IMinionLord l=m.getLord();
		if(l!=null&&l.getRepresentingEntity().equals(e))return true;
		return false;
	}
	
	/**
	 * Checks if the given lord is the minions lord. Contains null check.
	 * @param m
	 * @param l
	 * @return
	 */
	public static boolean isLordSafe(@Nullable IMinion m,@Nullable IMinionLord l){
		if(m==null||l==null)return false;
		return l.equals(m.getLord());
	}
	
	/**
	 * Sends a translated message to the lord if he exists and is a player
	 * @param m
	 * @param message
	 */
	public static void sendMessageToLord(IMinion m,String message){
		IMinionLord l=m.getLord();
		if(l!=null&&l.getRepresentingEntity() instanceof EntityPlayer){
			((EntityPlayer)l.getRepresentingEntity()).addChatComponentMessage(new ChatComponentTranslation(message));
		}
	}
}
