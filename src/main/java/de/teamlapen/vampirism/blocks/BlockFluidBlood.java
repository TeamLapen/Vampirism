package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;


public class BlockFluidBlood extends BlockFluidFinite {
    private final String name;

    public BlockFluidBlood(Fluid blood, String name) {
        super(blood, Material.WATER);
        this.name = name;
        blood.setBlock(this);
        setUnlocalizedName(blood.getUnlocalizedName());
        setRegistryName(REFERENCE.MODID, name);
    }

    public String getRegisteredName() {
        return name;
    }
}
