package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;

/**
 * Vampirism default block with set creative tab, registry name and unloc name
 */
public class VampirismBlock extends Block {

    public VampirismBlock(String regName, Block.Properties properties) {
        super(properties);
        setRegistryName(REFERENCE.MODID, regName);
    }


}
