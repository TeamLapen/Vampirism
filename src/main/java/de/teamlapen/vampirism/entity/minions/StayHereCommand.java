package de.teamlapen.vampirism.entity.minions;

public class StayHereCommand extends DefaultMinionCommand {
	protected final EntityRemoteVampireMinion minion;
	public StayHereCommand(int id,EntityRemoteVampireMinion minion) {
		super(id);
		this.minion=minion;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinV() {
		// TODO Auto-generated method stub
		return 0;
	}

}
