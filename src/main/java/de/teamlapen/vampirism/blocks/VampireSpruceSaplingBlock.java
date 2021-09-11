package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.world.gen.VampireSpruceTree;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class VampireSpruceSaplingBlock extends SaplingBlock {

    public VampireSpruceSaplingBlock() {
        super(new VampireSpruceTree(), Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    }
}
