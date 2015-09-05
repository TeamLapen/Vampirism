package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for skills which have a duration
 * 
 * @author maxanier
 */
public interface ILastingSkill extends ISkill {
	/**
	 * @param level
	 *            Vampire player level
	 * @return Skill duration in ticks
	 */
	public int getDuration(int level);

	/**
	 * Called when the skill is deactivated SERVER SIDE ONLY
	 * 
	 * @param vampire
	 * @param player
	 */
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player);

	/**
	 * Called when the skill is activated after a world reload. SERVER SIDE ONLY
	 * 
	 * @param vampire
	 * @param player
	 */
	public void onReActivated(VampirePlayer vampire, EntityPlayer player);

	/**
	 * Called every LivingUpdate for each entity which has this skill activated Calls on client side might be wrong due to sync
	 * 
	 * @param vampire
	 * @param player
	 * @return if true the skill is cancelled
	 */
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player);
}
