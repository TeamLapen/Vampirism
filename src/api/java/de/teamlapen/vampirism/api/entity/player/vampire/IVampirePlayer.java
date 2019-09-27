package de.teamlapen.vampirism.api.entity.player.vampire;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;

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

    boolean isVampireLord();

    /**
     * Try to sleep at the given location during daytime
     *
     * @param pos
     * @return
     */
    Either<PlayerEntity.SleepResult, Unit> trySleep(BlockPos pos);

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
     * Wake up the player if he is sleeping in a coffin
     *
     * @param immediately
     * @param updateWorldFlag
     * @param setSpawn
     */
    void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn);

    enum BITE_TYPE {
        ATTACK, ATTACK_HUNTER, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD_HUNTER_PLAYER, SUCK_BLOOD, NONE, HUNTER_CREATURE
    }
}
