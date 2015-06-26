package de.teamlapen.vampirism.entity.minions;

/**
 * Does not add any follow AI, this has to be added by the entity itself
 * 
 * @author Maxanier
 *
 */
public class JustFollowCommand extends DefaultMinionCommand {

	public JustFollowCommand(int id) {
		super(id);
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
		return "minioncommand.vampirism.justfollow";
	}

	@Override
	public void onActivated() {

	}

	@Override
	public void onDeactivated() {

	}

}
