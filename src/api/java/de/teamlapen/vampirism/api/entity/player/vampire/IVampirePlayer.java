package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * This method will be used to convert {@link net.minecraft.world.damagesource.DamageSource#IN_FIRE} and {@link net.minecraft.world.damagesource.DamageSource#ON_FIRE} to {@link de.teamlapen.vampirism.api.VReference#VAMPIRE_IN_FIRE} or respectively {@link de.teamlapen.vampirism.api.VReference#VAMPIRE_ON_FIRE}
     *
     * @param amount the unmodified fire damage amount
     * @return The modified amount
     */
    float calculateFireDamage(float amount);

    /**
     * @return The bite type which would be applied to the give entity
     */
    @NotNull
    BITE_TYPE determineBiteType(LivingEntity entity);

    /**
     * @return The players vampire skill handler
     */
    @NotNull
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
    @NotNull
    IBloodStats getBloodStats();

    @NotNull
    @Override
    default IPlayableFaction<IVampirePlayer> getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100.
     */
    int getTicksInSun();

    /**
     * TODO should this method be used somewhere?
     *
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    @SuppressWarnings("SameReturnValue")
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
     * @return Whether death event should be canceled
     */
    boolean onDeadlyHit(DamageSource source);

    /**
     * Locks the vision again, preventing the player from using it
     */
    void unUnlockVision(@NotNull IVampireVision vision);

    /**
     * Unlocks the given vision, so the player can activate it.
     * Is not saved to nbt
     */
    void unlockVision(@NotNull IVampireVision vision);

    enum BITE_TYPE {
        SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD, NONE, HUNTER_CREATURE
    }
}
