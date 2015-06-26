package de.teamlapen.vampirism.entity.minions;

import net.minecraft.entity.ai.EntityAIBase;
import de.teamlapen.vampirism.entity.ai.EntityAIMoveToLord;

public class ComeBackToPlayerCommand extends DefaultMinionCommand {

	private final EntityRemoteVampireMinion minion;
	private final EntityAIBase comeBack;

	public ComeBackToPlayerCommand(int id, EntityRemoteVampireMinion minion) {
		super(id);
		comeBack = new EntityAIMoveToLord(minion);
		this.minion = minion;
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
		return "minioncommand.vampirism.comeback";
	}

	@Override
	public void onActivated() {
		minion.tasks.addTask(3, comeBack);

	}

	@Override
	public void onDeactivated() {
		minion.tasks.removeTask(comeBack);

	}

}
