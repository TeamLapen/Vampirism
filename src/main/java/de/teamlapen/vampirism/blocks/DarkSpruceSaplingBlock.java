package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.world.gen.DarkSpruceTree;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class DarkSpruceSaplingBlock extends SaplingBlock {

    public DarkSpruceSaplingBlock() {
        super(new DarkSpruceTree(), Block.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }
}
