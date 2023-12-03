package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.EnumBloodSource;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

/**
 * Implemented by all vampire entities
 */
public interface IVampire extends IFactionEntity {

    /**
     * @return True if the entity is not affected by that garlic level
     */
    boolean doesResistGarlic(EnumStrength strength);

    /**
     * Adds blood to the vampires blood stats
     * Consume blood. Any remaining blood might be filled into blood bottles or used otherwise
     *
     * @param amt           In blood food unit, not mB. See {@link de.teamlapen.vampirism.api.VReference#FOOD_TO_FLUID_BLOOD} for conversion
     * @param saturationMod Similar to the food saturation modifier
     * @param bloodSource   The type of blood source used. See {@link de.teamlapen.vampirism.api.entity.player.vampire.EnumBloodSource}
     */
    default void drinkBlood(int amt, float saturationMod, EnumBloodSource bloodSource) {
        drinkBlood(amt, saturationMod, true, bloodSource);
    }

    /**
     * Should use {@link #drinkBlood(int, float, EnumBloodSource)} but this is provided for compatibility purposes.
     * Adds blood to the vampires blood stats
     * Consume blood. Any remaining blood might be filled into blood bottles or used otherwise
     *
     * @param amt           In blood food unit, not mB. See {@link de.teamlapen.vampirism.api.VReference#FOOD_TO_FLUID_BLOOD} for conversion
     * @param saturationMod Similar to the food saturation modifier
     */
    @Deprecated
    default void drinkBlood(int amt, float saturationMod) {
        drinkBlood(amt, saturationMod, true, EnumBloodSource.OTHER);
    }


    /**
     * Adds blood to the vampires blood stats.
     * If useRemaining is true, any remaining blood might be used otherwise. For example, it might be put into blood bottles
     *
     * @param amt           In blood food unit, not mB. See {@link de.teamlapen.vampirism.api.VReference#FOOD_TO_FLUID_BLOOD} for conversion
     * @param saturationMod Similar to the food saturation modifier
     */
    void drinkBlood(int amt, float saturationMod, boolean useRemaining, EnumBloodSource bloodSource);

    @NotNull
    @Override
    default IFaction<?> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    /**
     * @return Whether the entity is a skilled biter which  is able to suck blood more efficiently
     */
    default boolean isAdvancedBiter() {
        return false;
    }

    /**
     * Checks if the player is being affected by garlic.
     * Result is cached for a few ticks
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumStrength#NONE}
     *
     * @return The strength of the garlic or {@link EnumStrength#NONE}
     */
    @NotNull
    default EnumStrength isGettingGarlicDamage(LevelAccessor world) {
        return isGettingGarlicDamage(world, false);
    }

    /**
     * Checks if the player is being affected by garlic.
     * The result is cached for several ticks unless you use forceRefresh
     * Careful, this checks quite a large area of blocks and should not be refreshed to often
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns {@link EnumStrength#NONE}
     *
     * @param forceRefresh Don't use cached value
     * @return The strength of the garlic or {@link EnumStrength#NONE}
     */
    @NotNull
    EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forceRefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks unless you use forceRefresh
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns false
     *
     * @param forceRefresh Don't use cached value
     */
    boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh);

    /**
     * Checks if all requirements are met for the entity to be damaged by the sun, e.g. standing in the sun and not raining.
     * The result is cached for a few ticks.
     * <p>
     * For VampirePlayer instances for players with vampire level 0 this returns false
     */
    default boolean isGettingSundamage(LevelAccessor iWorld) {
        return isGettingSundamage(iWorld, false);
    }

    /**
     * If the entity currently does not care about being damaged by the sun, because it is e.g. angry or has sunscreen
     */
    boolean isIgnoringSundamage();

    /**
     * Consumes blood (removes).
     * Unless allowPartial is true, blood is only consumed if enough is available
     *
     * @param amt          In blood food unit, not mB. See {@link de.teamlapen.vampirism.api.VReference#FOOD_TO_FLUID_BLOOD} for conversion
     * @param allowPartial If true, the method removes as much blood as available up to the given limit
     * @return If amt was removed
     */
    boolean useBlood(int amt, boolean allowPartial);

    /**
     * @return If the creature wants blood or could use some
     */
    boolean wantsBlood();
}
