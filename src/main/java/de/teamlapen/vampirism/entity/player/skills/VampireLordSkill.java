package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class VampireLordSkill implements ILastingSkill {

	/**
	 * Skill ID, has to be set when this is registered
	 */
	public static int ID;
	
	@Override
	public int getMinLevel() {
		return BALANCE.VAMPIRE_PLAYER_LORD_MIN_LEVEL;
	}

	@Override
	public void onActivated(VampirePlayer vampire, EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCooldown() {
		return BALANCE.VAMPIRE_PLAYER_LORD_COOLDOWN*20;
	}

	@Override
	public int getDuration(int level) {
		return BALANCE.getVampireLordDuration(level);
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(VampirePlayer vampire, EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString(){
		return "VampireLordSkill ID: "+VampireLordSkill.ID;
	}

}
