package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class BlockCastleSlab extends BlockSlab {

    private static final String regName = "castle_slab";
    private final EnumVariant variant;

    public BlockCastleSlab(EnumVariant variant) {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
        this.variant = variant;

        this.setRegistryName(REFERENCE.MODID, regName + "_" + variant.getName());
    }


    public enum EnumVariant implements IStringSerializable {
        DARK_BRICK(0, "dark_brick"),
        PURPLE_BRICK(1, "purple_brick"),
        DARK_STONE(2, "dark_stone");

        /**
         * Array of the Block's BlockStates
         */
        private static final EnumVariant[] META_LOOKUP = new EnumVariant[values().length];

        static {
            for (EnumVariant blockstone$enumtype : values()) {
                META_LOOKUP[blockstone$enumtype.getMetadata()] = blockstone$enumtype;
            }
        }

        public static EnumVariant byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }
        /**
         * The BlockState's metadata.
         */
        private final int meta;
        /**
         * The EnumType's name.
         */
        private final String name;

        EnumVariant(int metaIn, String nameIn) {
            this.meta = metaIn;
            this.name = nameIn;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String getName() {
            return this.name;
        }


    }
}
