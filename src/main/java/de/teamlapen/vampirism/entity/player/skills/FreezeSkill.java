package de.teamlapen.vampirism.entity.player.skills;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;

public class FreezeSkill extends DefaultSkill {

	@Override
	public boolean canBeUsedBy(VampirePlayer vampire, EntityPlayer player) {
		return vampire.isVampireLord();
	}

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.FREEZE_COOLDOWN;
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.FREEZE_MIN_LEVEL;
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
	public String getUnlocalizedName() {
		return "skill.vampirism.freeze";
	}

	@Override
	public boolean onActivated(final VampirePlayer vampire, final EntityPlayer player) {
		List l = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(10, 5, 10), vampire.getMinionHandler().getLivingBaseSelectorExludingMinions());
		for (Object o : l) {
			EntityLivingBase e = (EntityLivingBase) o;
			e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			e.addPotionEffect(new PotionEffect(Potion.resistance.id, BALANCE.VP_SKILLS.FREEZE_DURATION * 20, 10));
			Helper.spawnParticlesAroundEntity(e, "snowshovel", 1.5, 40);
		}
		return false;
	}

}
