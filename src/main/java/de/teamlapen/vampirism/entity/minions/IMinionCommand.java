package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.util.IPieElement;

public interface IMinionCommand extends IPieElement{
	public int getId();
	
	public String getUnlocalizedName();
	
	public void onActivated(IMinion m);
	
	public void onDeactivated(IMinion m);
	
	public boolean canBeActivated(IMinion minion);
}
