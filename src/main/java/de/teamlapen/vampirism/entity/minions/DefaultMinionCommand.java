package de.teamlapen.vampirism.entity.minions;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Default implementation of IMinionCommand
 * 
 * @author Maxanier
 *
 */
public abstract class DefaultMinionCommand implements IMinionCommand {

	private final static ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/minion_commands.png");
	private final int id;

	public DefaultMinionCommand(int id) {
		this.id = id;
	}

	@Override
	public boolean canBeActivated() {
		return true;
	}

	@Override
	public ResourceLocation getIconLoc() {
		return defaultIcons;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean shouldPickupItem(@NonNull ItemStack item) {
		return false;
	}

	@Override
	public String toString() {
		return getUnlocalizedName() + ":" + id;
	}

}
