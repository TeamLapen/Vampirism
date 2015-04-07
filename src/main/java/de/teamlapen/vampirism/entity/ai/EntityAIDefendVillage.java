package de.teamlapen.vampirism.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class EntityAIDefendVillage extends EntityAITarget {

	EntityVampireHunter hunter;

	EntityLivingBase villageAgressorTarget;

	public EntityAIDefendVillage(EntityVampireHunter h) {
		super(h, false, false);
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
			} else if (villageAgressorTarget instanceof EntityZombie) {
				// Only attack Zombies if they can easily be reached, see EntityAITarget#canEasilyReach
				PathEntity pathentity = this.taskOwner.getNavigator().getPathToEntityLiving(villageAgressorTarget);

				if (pathentity == null) {
					return false;
				} else {
					PathPoint pathpoint = pathentity.getFinalPathPoint();

					if (pathpoint == null) {
						return false;
					} else {
						int i = pathpoint.xCoord - MathHelper.floor_double(villageAgressorTarget.posX);
						int j = pathpoint.zCoord - MathHelper.floor_double(villageAgressorTarget.posZ);
						return i * i + j * j <= 2.25D;
					}
				}
			} else if (villageAgressorTarget instanceof EntityVampire) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		this.hunter.setAttackTarget(this.villageAgressorTarget);
		super.startExecuting();
	}
}
