package de.teamlapen.vampirism.api.world.entity.player.vampire;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Interface for the player vampire data.
 * Attached to all players as capability
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer<IVampirePlayer>, IBiteableEntity, ISkillPlayer<IVampirePlayer>, IVampireVisionUser, IPlayer {

    /**
     * Increases exhaustion level by supplied amount
     */
    void addExhaustion(float exhaustion);

    /**
     * Vampires receive increased damage from fire.
     * This method will be used to convert {@link net.minecraft.world.damagesource.DamageTypes#IN_FIRE} and {@link net.minecraft.world.damagesource.DamageTypes#ON_FIRE} to {@link de.teamlapen.vampirism.core.ModDamageTypes#VAMPIRE_IN_FIRE} or respectively {@link de.teamlapen.vampirism.core.ModDamageTypes#VAMPIRE_ON_FIRE}
     *
     * @param amount the unmodified fire damage amount
     * @return The modified amount
     */
    @SuppressWarnings("JavadocReference")
    float calculateFireDamage(float amount);

    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(LivingEntity entity);

    /**
     * @return The players vampire skill handler
     */
    IActionHandler<IVampirePlayer> getActionHandler();

    @Override
    default Holder<? extends IPlayableFaction<IVampirePlayer>> getFaction() {
        return VampirismFactions.VAMPIRE;
    }

    int getBloodLevel();

    /**
     * @return The players blood stats (similar to food stats)
     */
    IBloodStats getBloodStats();

    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100.
     */
    int getTicksInSun();

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

    enum BITE_TYPE implements StringRepresentable {
        SUCK_BLOOD_CREATURE("suck_blood_creature"),
        SUCK_BLOOD_PLAYER("suck_blood_player"),
        SUCK_BLOOD("suck_blood"),
        NONE("none"),
        HUNTER_CREATURE("hunter_creature");

        private final String name;

        BITE_TYPE(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
