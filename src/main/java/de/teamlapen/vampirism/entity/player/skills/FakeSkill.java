package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

/**
 * Has no function. Only used to display a cancel section in the skill menu
 * @author maxanier
 *
 */
public class FakeSkill extends DefaultSkill {

	@Override
	public int getCooldown() {
		return 0;
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public int getMinU() {
		return 16;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public void onActivated(VampirePlayer vampire, EntityPlayer player) {

	}
	
	@Override
	public int getId(){
		return -1;
	}

}
