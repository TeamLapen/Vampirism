package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BloodySpruceLeavesBlock extends LeavesBlock {

    public BloodySpruceLeavesBlock(String regName) {
        super(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT));
        this.setRegistryName(REFERENCE.MODID, regName);
    }
}
