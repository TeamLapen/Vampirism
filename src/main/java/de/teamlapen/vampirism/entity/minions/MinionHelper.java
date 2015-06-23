package de.teamlapen.vampirism.entity.minions;

import org.eclipse.jdt.annotation.Nullable;

import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;

public class MinionHelper {
	public static @Nullable IMinion getMinionFromEntity(Entity e){
		if(e instanceof IMinion)return (IMinion) e;
		
		if(e instanceof EntityCreature){
			VampireMob m=VampireMob.get((EntityCreature) e);
			if(m.isMinion())return m;
		}
		return null;
	}
	
	public static boolean isLordPlayer(@Nullable IMinion m){
		if(m!=null&&m.getLord() instanceof VampirePlayer){
			return true;
		}
		return false;
	}
}
