package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;

/**
 * Part of the Altar of Infusion structure
 */
public class BlockAltarTip extends VampirismBlock {
    private final static String name = "altarTip";

    public BlockAltarTip() {
        super(name, Material.iron);
        setHarvestLevel("pickaxe", 1);
        this.setHardness(1.0F);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }


}
