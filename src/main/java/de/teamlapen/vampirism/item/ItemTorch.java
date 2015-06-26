package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;

public class ItemTorch extends BasicItem {
	public static final String name = "torch";

	public ItemTorch() {
		super("torch");
		this.setNoRepair();
		this.maxStackSize = 1;
		setCreativeTab(VampirismMod.tabVampirism);
		this.setFull3D();
	}

}
