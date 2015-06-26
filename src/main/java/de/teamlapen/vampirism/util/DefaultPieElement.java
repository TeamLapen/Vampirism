package de.teamlapen.vampirism.util;

import net.minecraft.util.ResourceLocation;

public class DefaultPieElement implements IPieElement {

	private final String unlocName;
	private final int minU,minV,id;
	public DefaultPieElement(int id,String unlocName, int minU, int minV, ResourceLocation resLoc) {
		super();
		this.unlocName = unlocName;
		this.minU = minU;
		this.minV = minV;
		this.id = id;
		this.resLoc = resLoc;
	}

	private final ResourceLocation resLoc;
	
	@Override
	public String getUnlocalizedName() {
		return unlocName;
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
	public ResourceLocation getIconLoc() {
		return resLoc;
	}

	@Override
	public int getId() {
		return id;
	}

}
