package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for the player vampire data.
 * Attached to all players as capability
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer<IVampirePlayer>, IBiteableEntity {

    /**
     * Force enables the vision
     * Does NOT unlock the vision
     *
     * @param vision Null to disable all
     */
    void activateVision(@Nullable IVampireVision vision);

    /**
     * Increases exhaustion level by supplied amount
     */
    void addExhaustion(float exhaustion);

    /**
     * Vampires receive increased damage from fire.
     * This method will be used to convert {@link net.minecraft.util.DamageSource#IN_FIRE} and {@link net.minecraft.util.DamageSource#ON_FIRE} to {@link de.teamlapen.vampirism.api.VReference#VAMPIRE_IN_FIRE} or respectivly {@link de.teamlapen.vampirism.api.VReference#VAMPIRE_ON_FIRE}
     *
     * @param amount the unmodified fire damage amount
     * @return The modified amount
     */
    float calculateFireDamage(float amount);

    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(LivingEntity entity);

    /**
     * @return The players vampire skill handler
     */
    IActionHandler<IVampirePlayer> getActionHandler();

    /**
     * @return The currently active vision. May be null
     */
    @Nullable
    IVampireVision getActiveVision();

    int getBloodLevel();

    /**
     * @return The players blood stats (similar to food stats)
     */
    IBloodStats getBloodStats();

    @Override
    default IPlayableFaction<IVampirePlayer> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100
     */
    int getTicksInSun();

    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();

    /**
     * @return Whether the player is in DBNO state (invulnerable to most damage, but unable to do things)
     */
    boolean isDBNO();

    /**
     * Check if the player should not die.
     * Initiates DBNO state if death prevented
     *
     * @param source The lethal damage source
     * @return Whether death event should be cancel
     */
    boolean onDeadlyHit(DamageSource source);

    /**
     * Locks the vision again, preventing the player from using it
     *
     * @param vision
     */
    void unUnlockVision(@Nonnull IVampireVision vision);

    /**
     * Unlocks the given vision, so the player can activate it.
     * Is not saved to nbt
     *
     * @param vision
     */
    void unlockVision(@Nonnull IVampireVision vision);

    /**
     * updates attributes of all minions
     */
    void updateMinionAttributes(boolean increasedStats);

    enum BITE_TYPE {
        @Deprecated ATTACK /* TODO 1.17 remove*/, @Deprecated ATTACK_HUNTER /* TODO 1.17 remove*/, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, @Deprecated SUCK_BLOOD_HUNTER_PLAYER /* TODO 1.17 remove */, SUCK_BLOOD, NONE, HUNTER_CREATURE
    }
}
