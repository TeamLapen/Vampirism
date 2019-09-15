package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class CastleSlabBlock extends SlabBlock {

    private static final String regName = "castle_slab";
    private final CastleBricksBlock.EnumVariant variant;

    public CastleSlabBlock(CastleBricksBlock.EnumVariant variant) {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        this.variant = variant;

        this.setRegistryName(REFERENCE.MODID, regName + "_" + variant.getName());
    }
}
