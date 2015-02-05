package de.teamlapen.vampirism.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;

public class EntityAIDefendVillage extends EntityAITarget {

	EntityVampireHunter hunter;
	/**
	 * The aggressor of the iron golem's village which is now the golem's attack
	 * target.
	 */
	EntityLivingBase villageAgressorTarget;

	public EntityAIDefendVillage(EntityVampireHunter h) {
		super(h, false, true);
		hunter = h;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		Village v = hunter.getHomeVillage();
		if (v == null)
			return false;

		this.villageAgressorTarget = v.findNearestVillageAggressor(hunter);

		if (this.isSuitableTarget(villageAgressorTarget, false)) {
			if (villageAgressorTarget instanceof EntityPlayer) {
				if (VampirePlayer.get((EntityPlayer) (villageAgressorTarget)).getLevel() > BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL) {
					return true;
				}
			} else if (villageAgressorTarget instanceof EntityVampire) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		Logger.i("test", "Starting to attack " + this.villageAgressorTarget.toString());
		this.hunter.setAttackTarget(this.villageAgressorTarget);
		super.startExecuting();
	}
}
