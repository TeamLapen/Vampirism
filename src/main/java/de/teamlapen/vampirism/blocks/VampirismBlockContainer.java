package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.ContainerBlock;

/**
 * Vampirism default block container with set creative tab, registry name and unloc name
 */
public abstract class VampirismBlockContainer extends ContainerBlock {


    public VampirismBlockContainer(String regName, Block.Properties properties) {
        super(properties);
        setRegistryName(REFERENCE.MODID, regName);

    }


}
