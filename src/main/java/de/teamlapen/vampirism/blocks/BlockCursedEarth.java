package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;


public class BlockCursedEarth extends VampirismBlock {

    private static final String name = "cursedEarth";

    public BlockCursedEarth() {
        super(name, Material.ground);
        this.setHardness(0.5F).setResistance(2.0F).setHarvestLevel("shovel", 0);
        setStepSound(soundTypeGravel);
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable instanceof BlockBush || plantable.getPlantType(world, pos).equals(VReference.vampirePlantType);
    }
}
