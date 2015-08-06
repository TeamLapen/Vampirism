package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class FreezeSkill extends DefaultSkill {

	@Override
	public boolean canBeUsedBy(VampirePlayer vampire, EntityPlayer player) {
		return vampire.isVampireLord();
	}

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.FREEZE_COOLDOWN * 20;
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.FREEZE_MIN_LEVEL;
	}

	@Override
	public int getMinU() {
		return 144;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "skill.vampirism.freeze";
	}

	@Override
	public boolean onActivated(final VampirePlayer vampire, final EntityPlayer player) {
		List l = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(10, 5, 10), vampire.getMinionHandler().getLivingBaseSelectorExludingMinions());
		for (Object o : l) {
			if (o instanceof EntityBlindingBat) continue;
			EntityLivingBase e = (EntityLivingBase) o;
			e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			e.addPotionEffect(new PotionEffect(Potion.resistance.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			e.addPotionEffect(new PotionEffect(Potion.jump.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 128));
			Helper.spawnParticlesAroundEntity(e, "snowshovel", 1.5, 40);
		}
		return l.size() > 0;
	}

}
