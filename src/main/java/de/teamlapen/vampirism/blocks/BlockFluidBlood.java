package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidFinite;


public class BlockFluidBlood extends BlockFluidFinite {
    private static final String name = "blockBloodFluid";

    public BlockFluidBlood() {
        super(ModFluids.blood, Material.water);
        setUnlocalizedName(ModFluids.blood.getUnlocalizedName());
        ModFluids.blood.setBlock(this);
        setRegistryName(REFERENCE.MODID, name);
    }
}
