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
	private int id;

	@Override
	public ResourceLocation getIconLoc() {
		return defaultIcons;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ":" + id;
	}

}
