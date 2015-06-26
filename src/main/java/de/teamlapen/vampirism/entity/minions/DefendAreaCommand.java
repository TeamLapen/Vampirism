package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class DefendAreaCommand extends DefaultMinionCommand {

	protected final EntityRemoteVampireMinion minion;
	protected final EntityAIBase stay;
	protected final EntityAIBase attack;
	protected final EntityAITarget target;
	private final int MAX_DISTANCE = 7;
	private ChunkCoordinates oldHome;
	private int oldDist;

	public DefendAreaCommand(int id, EntityRemoteVampireMinion minion) {
		super(id);
		this.minion = minion;
		stay = new EntityAIMoveTowardsRestriction(minion.getRepresentingEntity(), 1.0F);
		attack = new EntityAIAttackOnCollide(minion, EntityLivingBase.class, 1.0F, false);
		target = new EntityAINearestAttackableTarget(minion.getRepresentingEntity(), EntityMob.class, 0, true, true, MinionHelper.getEntitySelectorForMinion(minion, EntityMob.class, false, true));
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
			oldHome = minion.getHomePosition();
			oldDist = MathHelper.floor_float(minion.func_110174_bM());
		}
		minion.setHomeArea(MathHelper.floor_double(minion.posX), MathHelper.floor_double(minion.posY), MathHelper.floor_double(minion.posZ), MAX_DISTANCE);
	}

	@Override
	public void onDeactivated() {
		minion.tasks.removeTask(stay);
		minion.tasks.removeTask(attack);
		minion.targetTasks.removeTask(target);
		if (oldHome != null) {
			minion.setHomeArea(oldHome.posX, oldHome.posY, oldHome.posZ, oldDist);
		} else {
			minion.detachHome();
		}
		oldHome = null;
	}

}
