package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.entity.ai.EntityAIStayHere;

public class StayHereCommand extends DefaultMinionCommand {
	protected final EntityRemoteVampireMinion minion;
	protected final EntityAIStayHere stay;
	public StayHereCommand(int id,EntityRemoteVampireMinion minion) {
		super(id);
		this.minion=minion;
		stay=new EntityAIStayHere(minion);
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.stayhere";
	}

	@Override
	public void onActivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeactivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMinU() {
		return 80;
	}

	@Override
	public int getMinV() {
		return 0;
	}

}
