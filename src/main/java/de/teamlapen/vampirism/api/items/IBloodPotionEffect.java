package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.EntityLivingBase;

import java.util.Random;

/**
 * Interface for a Potion wrapper. Currently is not designed to be manually created. Will be created when registering something in {@link IBloodPotionRegistry}
 */
public interface IBloodPotionEffect {

    /**
     * @return The id this effect has been registed with
     */
    String getId();

    /**
     * @return The localized name
     */
    String getLocName(int duration, int amplifier);

    /**
     * Randomly selects duration and amplifier
     *
     * @param rnd
     * @return An array with [0]duration and [1]amplifier
     */
    int[] getRandomProperties(Random rnd);

    boolean isBad();

    /**
     * Called when this effect is activated
     */
    void onActivated(EntityLivingBase hunter, int duration, int amplifier);

    interface IPropertyRandomizer {
        /**
         * Randomly selects duration and amplifier
         *
         * @param rnd
         * @return An array with [0]duration and [1]amplifier
         */
        int[] getRandomProperties(Random rnd);
    }

    class SimpleRandomizer implements IPropertyRandomizer {
        private final int minDuration, maxDuration, amplifier;

        public SimpleRandomizer(int minDuration, int maxDuration, int amplifier) {
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
            this.amplifier = amplifier;
        }

        @Override
        public int[] getRandomProperties(Random rnd) {
            return new int[]{(minDuration + rnd.nextInt(maxDuration - minDuration) + 1), amplifier};
        }
    }
}
