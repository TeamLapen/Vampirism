package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.entity.ai.MinionAIDefendLord;
import net.minecraft.entity.ai.EntityAITarget;

public class DefendLordCommand extends DefaultMinionCommand {

	protected final IMinion m;
	protected final EntityAITarget defend;

	public DefendLordCommand(int id, IMinion m) {
		super(id);
		this.m = m;
		defend = new MinionAIDefendLord(m);
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
