package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class InvisibilitySkill extends DefaultSkill implements ILastingSkill {

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.INVISIBILITY_COOLDOWN*20;
	}

	@Override
	public int getMinU() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinV() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onActivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(true);

	}

	@Override
	public int getDuration(int level) {
		return BALANCE.VP_SKILLS.INVISIBILITY_DURATION*20;
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(false);

	}

	@Override
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player) {
		if(!player.isInvisible()){
			player.setInvisible(true);
		}
		return false;
	}

	@Override
	public void onReActivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(true);

	}
	
	@Override
	public boolean canBeUsedBy(VampirePlayer vampire,EntityPlayer player){
		return vampire.isVampireLord();
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

}
