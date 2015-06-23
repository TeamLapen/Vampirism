package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;
import net.minecraft.entity.ai.EntityAITarget;

public class DefendLordCommand extends DefaultMinionCommand {
	
	protected final IMinion m;
	protected final EntityAITarget defend;
	
	public DefendLordCommand(int id,IMinion m) {
		super(id);
		this.m=m;
		defend=new EntityAIDefendLord(m);
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

}
