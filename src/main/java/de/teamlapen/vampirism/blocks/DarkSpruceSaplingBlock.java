package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.gen.DarkSpruceTree;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class DarkSpruceSaplingBlock extends SaplingBlock {

    public DarkSpruceSaplingBlock() {
        super(new DarkSpruceTree(), Block.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
        this.setRegistryName(REFERENCE.MODID, "dark_spruce_sapling");
    }
}
