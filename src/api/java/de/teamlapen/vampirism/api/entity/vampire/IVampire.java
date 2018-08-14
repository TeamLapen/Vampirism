package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

import javax.annotation.Nonnull;

/**
 * Implemented by all vampire entities
 */
public interface IVampire extends IFactionEntity {

    /**
     * @param strength
     * @return True if the entity is not affected by that garlic level
     */
    boolean doesResistGarlic(EnumStrength strength);

    /**
     * Consume blood
     *
     * @param amt           In blood food unit, not mB. See {@link de.teamlapen.vampirism.api.VReference#FOOD_TO_FLUID_BLOOD} for conversion
     * @param saturationMod
     */
    void drinkBlood(int amt, float saturationMod);

    @Override
    default IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    /**
     * Checks if the player is being affected by garlic.
     * Result is cached for a few ticks
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumStrength#NONE}
     *
     * @return The strength of the garlic or {@link EnumStrength#NONE}
     */
    @Nonnull
    default EnumStrength isGettingGarlicDamage() {
        return isGettingGarlicDamage(false);
    }

    /**
     * Checks if the player is being affected by garlic.
     * The result is cached for several ticks unless you use forcerefresh
     * Careful, this checks quite a large area of blocks and should not be refreshed to often
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumStrength#NONE}
     *
     * @param forceRefresh Don't use cached value
     * @return The strength of the garlic or {@link EnumStrength#NONE}
     */
    @Nonnull
    EnumStrength isGettingGarlicDamage(boolean forceRefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks unless you use forcerefresh
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns false
     *
     * @param forceRefresh Don't use cached value
     */
    boolean isGettingSundamage(boolean forceRefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks.
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns false
     */
    default boolean isGettingSundamage() {
        return isGettingSundamage(false);
    }

    /**
     * If the entity currently does not care about being damaged by the sun, because it is e.g. angry or has sunscreen
     */
    boolean isIgnoringSundamage();

    /**
     * @return If the creature wants blood or could use some
     */
    boolean wantsBlood();
}
