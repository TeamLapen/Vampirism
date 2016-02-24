package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

/**
 * Implemented by all vampire entities
 */
public interface IVampire extends IFactionEntity {
    void consumeBlood(int amt, float saturationMod);

    /**
     *
     * @param strength
     * @return True if the entity is not affected by that garlic level
     */
    boolean doesResistGarlic(EnumGarlicStrength strength);

    /**
     * Checks if the player is being affected by garlic.
     * Result is cached for a few ticks
     * Recommend implementation: Just call isGettingGarlicDamage(false)
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumGarlicStrength#NONE}
     *
     * @return The strength of the garlic or {@link EnumGarlicStrength#NONE}
     */
    EnumGarlicStrength isGettingGarlicDamage();


    /**
     * Checks if the player is being affected by garlic.
     * The result is cached for several ticks unless you use forcerefresh
     * Careful, this checks quite a large area of blocks and should not be refreshed to often
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumGarlicStrength#NONE}
     *
     * @return The strength of the garlic or {@link EnumGarlicStrength#NONE}
     */
    EnumGarlicStrength isGettingGarlicDamage(boolean forcerefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks unless you use forcerefresh
     *
     * For VampirePlayer instances for players with vampire level 0 this returns false
     * @param forcerefresh
     * @return
     */
    boolean isGettingSundamage(boolean forcerefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks.
     * Recommend implementation: Just call isGettingSundamage(false)
     *
     * For VampirePlayer instances for players with vampire level 0 this returns false
     *
     * @return
     */
    boolean isGettingSundamage();
}
