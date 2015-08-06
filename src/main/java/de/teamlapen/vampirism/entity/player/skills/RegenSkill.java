package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class RegenSkill extends DefaultSkill {

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.REGEN_COOLDOWN * 20;
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.REGEN_MIN_LEVEL;
	}

	@Override
	public int getMinU() {
		return 80;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "skill.vampirism.regen";
	}

	@Override
	public boolean onActivated(VampirePlayer vampire, EntityPlayer player) {
		int dur = BALANCE.VP_SKILLS.REGEN_DURATION * 20;
		player.addPotionEffect(new PotionEffect(Potion.regeneration.id, dur, 0));
		player.addPotionEffect(new PotionEffect(ModPotion.thirst.id, dur, 2));
		return true;
	}

	@Override
	public String toString() {
		return "RegenSkill";
	}

}
