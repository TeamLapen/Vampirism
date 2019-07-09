package de.teamlapen.vampirism.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
    public int getHarvestLevel(BlockState p_getHarvestLevel_1_) {
        return 1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState p_getHarvestTool_1_) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }



}
