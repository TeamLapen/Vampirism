package de.teamlapen.vampirism.api.world.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

/**
 * A class containing static properties of a quarrel.
 */
public record QuarrelProperties(int color, Component effectDescription, AbstractArrow.Pickup pickupBehavior,
                                float baseDamage, float damageMultiplier, float knockbackMultiplier,
                                float velocityFactor, float gravityFactor, float inaccuracyFactor,
                                int extraPierceLevel, float chargeMultiplier, boolean forcesChunkLoading) {

    public static final QuarrelProperties DEFAULT = of(0xFFFFFFFF).build();

    public static Builder of(int color) {
        return new Builder(color);
    }

    public static class Builder {

        private final int color;
        private Component effectDescription = Component.empty();
        private AbstractArrow.Pickup pickupBehavior = AbstractArrow.Pickup.CREATIVE_ONLY;
        private float baseDamage = 0f;
        private float damageMultiplier = 1f;
        private float knockbackMultiplier = 1f;
        private float velocityFactor = 1f;
        private float gravityFactor = 1f;
        private float inaccuracyFactor = 1f;
        private int extraPierceLevel = 0;
        private float chargeMultiplier = 1f;
        private boolean forcesChunkLoading = false;

        private Builder(int color) {
            this.color = color;
        }

        /**
         * Tooltip line shown under the "On Impact" header. Defaults to {@link Component#empty()} (no tooltip).
         */
        public Builder effectDescription(Component description) {
            this.effectDescription = description;
            return this;
        }

        /**
         * Whether the spent quarrel can be picked up. Defaults to {@link AbstractArrow.Pickup#CREATIVE_ONLY}.
         */
        public Builder pickupBehavior(AbstractArrow.Pickup behavior) {
            this.pickupBehavior = behavior;
            return this;
        }

        /**
         * Base damage of the quarrel before it is affected by speed and other modifiers. Defaults to 0.
         */
        public Builder baseDamage(float damage) {
            this.baseDamage = damage;
            return this;
        }

        /**
         * Multiplier applied to the damage the quarrel deals. 1 = no changes, 0 = no damage.
         */
        public Builder damageMultiplier(float multiplier) {
            this.damageMultiplier = multiplier;
            return this;
        }

        /**
         * Multiplier applied to the knockback the quarrel imparts on an entity hit. 1 = vanilla knockback, 0 = none.
         */
        public Builder knockbackMultiplier(float multiplier) {
            this.knockbackMultiplier = multiplier;
            return this;
        }

        /**
         * Multiplier applied to the launch speed. Values below 1 make the quarrel slower (and, since arrow damage scales
         * with speed, softer at range). 1 = vanilla speed.
         */
        public Builder velocityFactor(float factor) {
            this.velocityFactor = factor;
            return this;
        }

        /**
         * Multiplier applied to the quarrel's gravity, composed on top of the crossbow's precision factor. Values above
         * 1 make it drop faster (heavier). 1 = unchanged.
         */
        public Builder gravityFactor(float factor) {
            this.gravityFactor = factor;
            return this;
        }

        /**
         * Multiplier applied to the shot's inaccuracy. Values below 1 tighten the grouping. 1 = unchanged.
         */
        public Builder inaccuracyFactor(float factor) {
            this.inaccuracyFactor = factor;
            return this;
        }

        /**
         * Pierce levels granted innately, added on top of the weapon's Piercing enchantment. 0 = none.
         */
        public Builder extraPierceLevel(int level) {
            this.extraPierceLevel = level;
            return this;
        }

        /**
         * Multiplier applied to the crossbow's charge time when this quarrel is the selected/loaded ammo. Above 1 takes
         * longer to charge. 1 = unchanged.
         */
        public Builder chargeMultiplier(float multiplier) {
            this.chargeMultiplier = multiplier;
            return this;
        }

        /**
         * Makes the quarrel entity keep the chunks along its flight path loaded, like an ender pearl.
         * This is only needed for the teleport quarrel, do not use for normal quarrels unless necessary; defaults to false.
         */
        public Builder forcesChunkLoading() {
            this.forcesChunkLoading = true;
            return this;
        }

        public QuarrelProperties build() {
            return new QuarrelProperties(color, effectDescription, pickupBehavior, baseDamage, damageMultiplier, knockbackMultiplier, velocityFactor, gravityFactor, inaccuracyFactor, extraPierceLevel, chargeMultiplier, forcesChunkLoading);
        }
    }
}