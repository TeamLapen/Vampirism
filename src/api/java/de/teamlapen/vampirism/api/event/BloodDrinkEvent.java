package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public abstract class BloodDrinkEvent extends Event {
    @NotNull
    private final IVampirePlayer vampirePlayer;
    private int amount;
    private float saturation;


    private BloodDrinkEvent(@NotNull IVampirePlayer player, int amount, float saturation) {
        this.vampirePlayer = player;
        this.amount = amount;
        this.saturation = saturation;
    }

    /**
     * @return The VampirePlayer that is biting.
     */
    @NotNull
    public IVampirePlayer getVampirePlayer() {
        return this.vampirePlayer;
    }
    /**
     * @return The amount of the blood that is drained.
     */
    public int getAmount() {
        return this.amount;
    }
    /**
     * @return The saturation modifier that is gained.
     */
    public float getSaturation() {
        return this.saturation;
    }

    /**
     * @param amount The new amount of blood drained
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     *
     * @param saturation the new saturation modifier
     */
    public void setSaturationModifier(float saturation) {
        this.saturation = saturation;
    }

    /**
     * Posted whenever a Vampire Player feeds by biting another mob.
     * This does not change the amount drained from the entity, meaning it only changes the amount of blood the vampire receives from biting.
     */
    public static class BiteFeedEvent extends BloodDrinkEvent {
        private final LivingEntity target;
        public BiteFeedEvent(@NotNull IVampirePlayer player, LivingEntity target, int amount, float saturation) {
            super(player,amount, saturation);
            this.target = target;
        }

        /**
         * @return The LivingEntity that is being bitten
         */
        @NotNull
        public LivingEntity getTarget() {
            return this.target;
        }
    }

    /**
     * Posted whenever a Vampire Player drinks blood, such as when drinking blood from a bottle, eating a human heart or biting another mob
     */
    public static class PlayerDrinkBloodEvent extends BloodDrinkEvent {
        private boolean useRemaining;

        public PlayerDrinkBloodEvent(@NotNull IVampirePlayer player, int amount, float saturation, boolean useRemaining) {
            super(player, amount, saturation);
            this.useRemaining = useRemaining;
        }
        /**
         * @return Whether the remaining blood should be used, see {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean)}.
         */
        public boolean useRemaining() {
            return this.useRemaining;
        }
        /**
         *
         * @param useRemaining the new useRemaining
         */
        public void setUseRemaining(boolean useRemaining) {
            this.useRemaining = useRemaining;
        }
    }


}
