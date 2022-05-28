package de.teamlapen.vampirism.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class DarkSpruceLeavesBlock extends LeavesBlock {

    public DarkSpruceLeavesBlock() {
        super(Block.Properties.of(Material.LEAVES, MaterialColor.COLOR_BLACK).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion());
        ((FireBlock) Blocks.FIRE).setFlammable(this, 30, 60);
    }
}
