package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class VampireRageSkill extends DefaultSkill implements ILastingSkill {


	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.RAGE_COOLDOWN * 20;
	}

	@Override
	public int getDuration(int level) {
		return BALANCE.VP_SKILLS.getVampireLordDuration(level);
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.RAGE_MIN_LEVEL;
	}

	@Override
	public int getMinU() {
		return 32;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public void onActivated(VampirePlayer vampire, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, getDuration(vampire.getLevel()), 2));

	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
		player.removePotionEffect(Potion.moveSpeed.id);

	}

	@Override
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player) {
		return false;
	}

}
