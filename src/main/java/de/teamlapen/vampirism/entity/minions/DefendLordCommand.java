package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.ai.EntityAITarget;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;

public class DefendLordCommand extends DefaultMinionCommand {

	protected final IMinion m;
	protected final EntityAITarget defend;

	public DefendLordCommand(int id, IMinion m) {
		super(id);
		this.m = m;
		defend = new EntityAIDefendLord(m);
	}

	@Override
	public int getMinU() {
		return 64;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.defendlord";
	}

	@Override
	public void onActivated() {
		m.getRepresentingEntity().targetTasks.addTask(2, defend);

	}

	@Override
	public void onDeactivated() {
		m.getRepresentingEntity().targetTasks.removeTask(defend);

	}

}
