package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.gen.BloodySpruceTree;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class BloodySpruceSaplingBlock extends SaplingBlock {

    public BloodySpruceSaplingBlock() {
        super(new BloodySpruceTree(), Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
        this.setRegistryName(REFERENCE.MODID, "bloody_spruce_sapling");
    }
}
