package de.teamlapen.vampirism.entity.minions;

import org.eclipse.jdt.annotation.NonNull;

import net.minecraft.item.ItemStack;
import de.teamlapen.vampirism.util.IPieElement;

public interface IMinionCommand extends IPieElement{
	public int getId();
	
	public String getUnlocalizedName();
	
	public void onActivated();
	
	public void onDeactivated();
	
	public boolean canBeActivated();
	
	public boolean shouldPickupItem(@NonNull ItemStack item);
}
