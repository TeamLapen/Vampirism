package de.teamlapen.vampirism.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Element of {@link de.teamlapen.vampirism.client.gui.GUIPieMenu}
 *
 */
public interface IPieElement {
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

	public String getUnlocalizedName();
}
