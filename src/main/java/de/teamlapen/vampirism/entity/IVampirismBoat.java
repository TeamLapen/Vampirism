package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * must be extended by a class that extend {@link net.minecraft.world.entity.Entity}
 */
public interface IVampirismBoat {

    void setType(BoatType type);

    BoatType getBType();

    enum BoatType {
        DARK_SPRUCE(ModBlocks.DARK_SPRUCE_PLANKS, "dark_spruce"),
        CURSED_SPRUCE(ModBlocks.CURSED_SPRUCE_PLANKS, "cursed_spruce");

        private final String name;
        private final Supplier<Block> planks;

        BoatType(Supplier<Block> planks, String name) {
            this.name = name;
            this.planks = planks;
        }

        public String getName() {
            return this.name;
        }

        public Block getPlanks() {
            return planks.get();
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static BoatType byId(int id) {
            BoatType[] values = values();
            if (id < 0 || id >= values.length) {
                id = 0;
            }
            return values[id];
        }

        public static BoatType byName(String name) {
            BoatType[] types = values();
            for (BoatType type : types) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return types[0];
        }
    }
}
