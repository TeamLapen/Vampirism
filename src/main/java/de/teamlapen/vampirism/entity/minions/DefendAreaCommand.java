package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class DefendAreaCommand extends DefaultMinionCommand {

	protected final EntityRemoteVampireMinion minion;
	protected final EntityAIBase stay;
	protected final EntityAIBase attack;
	protected final EntityAITarget target;
	private final int MAX_DISTANCE = 7;
	private AxisAlignedBB oldHome;

	public DefendAreaCommand(int id, EntityRemoteVampireMinion minion) {
		super(id);
		this.minion = minion;
		stay = new EntityAIMoveTowardsRestriction(minion.getRepresentingEntity(), 1.0F);
		attack = new EntityAIAttackOnCollide(minion, EntityLivingBase.class, 1.0F, false);
		target = new EntityAINearestAttackableTarget(minion.getRepresentingEntity(), EntityMob.class, 0, true, true, MinionHelper.getPredicateForMinion(minion, EntityMob.class, false, true));
	}

	@Override
	public int getMinU() {
		return 96;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.defendarea";
	}

	@Override
	public void onActivated() {
		minion.tasks.addTask(1, stay);
		minion.tasks.addTask(2, attack);
		minion.targetTasks.addTask(2, target);
		if (minion.hasHome()) {
			oldHome = minion.getHome();
		}
		minion.setHomeArea(MathHelper.floor_double(minion.posX), MathHelper.floor_double(minion.posY), MathHelper.floor_double(minion.posZ), MAX_DISTANCE);
	}

	@Override
	public void onDeactivated() {
		minion.tasks.removeTask(stay);
		minion.tasks.removeTask(attack);
		minion.targetTasks.removeTask(target);
		if (oldHome != null) {
			minion.setHome(oldHome);
		} else {
			minion.detachHome();
		}
		oldHome = null;
	}

}
