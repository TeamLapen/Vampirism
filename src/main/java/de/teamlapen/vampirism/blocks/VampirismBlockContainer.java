package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends BlockContainer {


    public VampirismBlockContainer(String regName, Block.Properties properties) {
        super(properties);
        setRegistryName(REFERENCE.MODID, regName);

    }


}
