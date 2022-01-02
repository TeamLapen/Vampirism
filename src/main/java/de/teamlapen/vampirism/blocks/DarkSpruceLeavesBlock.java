package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class DarkSpruceLeavesBlock extends LeavesBlock {

    public DarkSpruceLeavesBlock(String regName) {
        super(Block.Properties.of(Material.LEAVES, MaterialColor.CRIMSON_HYPHAE).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion());
        this.setRegistryName(REFERENCE.MODID, regName);
        ((FireBlock) Blocks.FIRE).setFlammable(this, 30, 60);
    }
}
