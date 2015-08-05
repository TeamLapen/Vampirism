package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.player.EntityPlayer;

public class InvisibilitySkill extends DefaultSkill implements ILastingSkill {

	@Override
	public boolean canBeUsedBy(VampirePlayer vampire, EntityPlayer player) {
		return vampire.isVampireLord();
	}

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.INVISIBILITY_COOLDOWN * 20;
	}

	@Override
	public int getDuration(int level) {
		return BALANCE.VP_SKILLS.INVISIBILITY_DURATION * 20;
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public int getMinU() {
		return 128;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "skill.vampirism.invisibility";
	}

	@Override
	public boolean onActivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(true);
		return true;
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(false);

	}

	@Override
	public void onReActivated(VampirePlayer vampire, EntityPlayer player) {
		player.setInvisible(true);

	}

	@Override
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player) {
		if (!player.isInvisible()) {
			player.setInvisible(true);
		}
		return false;
	}

}
