

package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.block.BlockStringProp;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;

/**
 * BlockStringProp with creative tab, registry name and unloc name
 */
public class VampirismBlockString extends BlockStringProp {
    public VampirismBlockString(String regName, Material materialIn, String[] values, String propName) {
        super(materialIn, values, propName);
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    public VampirismBlockString(String regName, Material material, String[] values) {
        this(regName, material, values, defaultPropName);

    }
}
