package de.teamlapen.vampirism.api.items;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

/**
 * Randomizer used for the simple blood potion effects
 */
public interface IBloodPotionPropertyRandomizer {
    /**
     * Randomly selects duration and amplifier etc
     *
     * @param rnd Used for random generation
     * @return A nbt tag containing all properties which can be used to store the effect with the item
     */
    CompoundNBT getRandomProperties(Random rnd);

    /**
     * Simple implementation
     */
    class SimpleRandomizer implements IBloodPotionPropertyRandomizer {
        private final int minDuration, maxDuration, amplifier;

        public SimpleRandomizer(int minDuration, int maxDuration, int amplifier) {
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
            this.amplifier = amplifier;
        }

        @Override
        public CompoundNBT getRandomProperties(Random rnd) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("duration", (minDuration + rnd.nextInt(maxDuration - minDuration) + 1));
            nbt.putInt("amplifier", amplifier);
            return nbt;
        }
    }
}
