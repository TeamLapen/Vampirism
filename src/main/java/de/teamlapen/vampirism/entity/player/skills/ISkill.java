package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

/**
 * Interface for vampire skills
 * 
 * @author maxanier
 *
 */
public interface ISkill {
	/**
	 * @return Cooldown time in ticks until the skill can be used again
	 */
	public int getCooldown();

	/**
	 * @return The minimum level which is required to use this skill
	 */
	public int getMinLevel();

	/**
	 * Called when the skill is activated SERVER SIDE ONLY
	 * 
	 * @param vampire
	 * @param player
	 */
	public void onActivated(VampirePlayer vampire, EntityPlayer player);
}
