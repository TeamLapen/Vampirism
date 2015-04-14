package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class BatSkill extends DefaultSkill implements ILastingSkill {
	public final static float BAT_WIDTH=0.5F;
	public final static float BAT_HEIGHT=0.9F;
	public final static float BAT_EYE_HEIGHT=0.85F*BAT_HEIGHT;
	public final static float PLAYER_WIDTH=0.8F;
	public final static float PLAYER_HEIGHT=1.8F;
	@Override
	public int getCooldown() {
		return 1;
	}

	@Override
	public int getMinLevel() {
		return 0;
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
	}

	@Override
	public int getDuration(int level) {
		return 100000;
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
	}

	@Override
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

}
