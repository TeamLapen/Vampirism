package de.teamlapen.vampirism.entity.minions;

public class StayHereCommand extends DefaultMinionCommand {

	public StayHereCommand(int id) {
		super(id);
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.stayhere";
	}

	@Override
	public void onActivated(IMinion m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeactivated(IMinion m) {
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
