package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

/**
 * Vampirism's flowers. To add one add it to {@link TYPE}
 */
public class VampirismFlower extends BlockBush {
    private final static String regName = "vampirism_flower";
    private final TYPE type;

    public VampirismFlower(TYPE type) {
        super(Properties.create(Material.PLANTS).hardnessAndResistance(0).doesNotBlockMovement().sound(SoundType.PLANT));
        this.type = type;
        setRegistryName(REFERENCE.MODID, regName + "_" + type.getName());

    }


    public enum TYPE implements IStringSerializable {

        ORCHID(0, "vampire_orchid", "vampire_orchid");


        private final int meta;
        private final String name;
        private final String unlocalizedName;

        TYPE(int meta, String name, String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }

    }
}
