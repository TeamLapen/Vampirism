package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
	 * Should return the location of the icon map where the icon is in
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconLoc();

	/**
	 * @return The assigned Id
	 */
	public int getId();

	/**
	 * @return The minimum level which is required to use this skill
	 */
	public int getMinLevel();

	/**
	 * Should return the min U texture coordinate within the icon map
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getMinU();

	/**
	 * Should return the min V texture coordinate within the icon map
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getMinV();

	/**
	 * Called when the skill is activated SERVER SIDE ONLY
	 * 
	 * @param vampire
	 * @param player
	 */
	public void onActivated(VampirePlayer vampire, EntityPlayer player);

	/**
	 * Should only be called when being registered
	 * 
	 * @param id
	 */
	public void setId(int id);
}
