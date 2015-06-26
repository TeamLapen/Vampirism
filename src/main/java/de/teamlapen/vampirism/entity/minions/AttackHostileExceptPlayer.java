package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;

/**
 * Makes the minion attack EntityMobs. Does not attack minions of other players. Only attacks vampires if the minions lord is a vampire lord.
 * 
 * @author Maxanier
 *
 */
public class AttackHostileExceptPlayer extends DefaultMinionCommand {

	protected final IMinion minion;
	protected final EntityAITarget attack;

	public AttackHostileExceptPlayer(int id, IMinion m) {
		super(id);
		minion = m;

		attack = new EntityAINearestAttackableTarget(m.getRepresentingEntity(), EntityMob.class, 0, true, false, MinionHelper.getEntitySelectorForMinion(minion, EntityMob.class, false, true));
	}

	@Override
	public int getMinU() {
		return 0;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.attackhostilenoplayers";
	}

	@Override
	public void onActivated() {
		minion.getRepresentingEntity().targetTasks.addTask(3, attack);

	}

	@Override
	public void onDeactivated() {
		minion.getRepresentingEntity().targetTasks.removeTask(attack);

	}

}
