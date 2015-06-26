package de.teamlapen.vampirism.util;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Element of {@link de.teamlapen.vampirism.client.gui.GUIPieMenu}
 *
 */
public interface IPieElement {
	public String getUnlocalizedName();
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
}
