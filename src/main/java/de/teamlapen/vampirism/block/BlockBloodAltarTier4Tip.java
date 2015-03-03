package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;


/**
 * Pillar tip for altar tier 4
 * @author Maxanier
 *
 */
public class BlockBloodAltarTier4Tip extends BasicBlock{

	public final static String name="bloodAltarTier4Tip";
	public BlockBloodAltarTier4Tip() {
		super(Material.rock, name);
		this.setBlockTextureName("bookshelf");//TODO change
	}

}
