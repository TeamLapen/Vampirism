package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;

public class BloodySpruceLeavesBlock extends LeavesBlock {

    public BloodySpruceLeavesBlock(String regName) {
        super(Block.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion());
        this.setRegistryName(REFERENCE.MODID, regName);
        ((FireBlock) Blocks.FIRE).setFlammable(this, 30, 60);
    }
}
