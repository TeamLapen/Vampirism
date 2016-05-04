package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

/**
 * Part of the Altar of Infusion structure
 */
public class BlockAltarTip extends VampirismBlock {
    private final static String name = "altarTip";

    public BlockAltarTip() {
        super(name, Material.IRON);
        setHarvestLevel("pickaxe", 1);
        this.setHardness(1.0F);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


}
