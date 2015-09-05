package de.teamlapen.vampirism.util;

import net.minecraft.util.ResourceLocation;
import org.eclipse.jdt.annotation.Nullable;

public class DefaultPieElement implements IPieElement {

	private final String unlocName;
	private final int minU, minV, id;
	private final float[] color;

	private final ResourceLocation resLoc;

	public DefaultPieElement(int id, String unlocName, int minU, int minV, ResourceLocation resLoc) {
		this(id, unlocName, minU, minV, resLoc, null);
	}

	/**
	 * Creates a simple instance of IPieElement
	 * 
	 * @param id
	 * @param unlocName
	 * @param minU
	 * @param minV
	 * @param resLoc
	 * @param color
	 *            Can be checked by menu-gui to set the border color. Has to be an array of size 3 with values between 0 and 1
	 */
	public DefaultPieElement(int id, String unlocName, int minU, int minV, ResourceLocation resLoc, float[] color) {
		super();
		this.unlocName = unlocName;
		this.minU = minU;
		this.minV = minV;
		this.id = id;
		this.resLoc = resLoc;
		this.color = color;
	}

	public @Nullable float[] getColor() {
		return this.color;
	}

	@Override
	public ResourceLocation getIconLoc() {
		return resLoc;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getMinU() {
		return minU;
	}

	@Override
	public int getMinV() {
		return minV;
	}

	@Override
	public String getUnlocalizedName() {
		return unlocName;
	}

}
