package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.BloodySpruceTree;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BloodySpruceSaplingBlock extends SaplingBlock {

    public BloodySpruceSaplingBlock() {
        super(new BloodySpruceTree(), Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
        this.setRegistryName(REFERENCE.MODID, "bloody_spruce_sapling");
    }
}
