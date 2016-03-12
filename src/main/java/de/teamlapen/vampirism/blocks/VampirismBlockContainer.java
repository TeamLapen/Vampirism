package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends BlockContainer {
    public VampirismBlockContainer(String regName, Material materialIn) {
        super(materialIn);
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

}
