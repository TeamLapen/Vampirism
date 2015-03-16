package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.util.ResourceLocation;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Default implementation of ISkill, provides some standard methods
 * 
 * @author maxanier
 *
 */
public abstract class DefaultSkill implements ISkill {
	private final static ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/skills.png");

	@Override
	public ResourceLocation getIconLoc() {
		return defaultIcons;
	}

}
