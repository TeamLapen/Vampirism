package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * Part of the Altar of Infusion structure
 */
public class BlockAltarTip extends VampirismBlock {
    private final static String name = "altar_tip";

    public BlockAltarTip() {
        super(name, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Override
    public int getHarvestLevel(IBlockState p_getHarvestLevel_1_) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(IBlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }



}
