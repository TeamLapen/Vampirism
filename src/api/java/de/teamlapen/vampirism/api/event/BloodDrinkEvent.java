package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.vampire.EnumBloodSource;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public abstract class BloodDrinkEvent extends Event {
    @NotNull
    private final IVampire vampire;
    private int amount;
    private float saturation;
    private final EnumBloodSource bloodSource;


    private BloodDrinkEvent(@NotNull IVampire vampire, int amount, float saturation, EnumBloodSource bloodSource) {
        this.vampire = vampire;
        this.amount = amount;
        this.saturation = saturation;
        this.bloodSource = bloodSource;
    }

    /**
     * @return The VampirePlayer that is biting.
     */
    @NotNull
    public IVampire getVampire() {
        return this.vampire;
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
     * @return The type of blood source that the blood was obtained from.
     */
    public EnumBloodSource getBloodSource() {
        return this.bloodSource;
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
            super(player,amount, saturation, EnumBloodSource.BITE_FEED);
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

        public PlayerDrinkBloodEvent(@NotNull IVampirePlayer player, int amount, float saturation, boolean useRemaining, EnumBloodSource bloodSource) {
            super(player, amount, saturation, bloodSource);
            this.useRemaining = useRemaining;
        }
        /**
         * @return Whether the remaining blood should be used, see {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean, de.teamlapen.vampirism.api.entity.player.vampire.EnumBloodSource)}.
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
    /**
     * Posted whenever any non-player vampire entity drinks any blood at all.
     */
    public static class EntityDrinkBloodEvent extends BloodDrinkEvent {

        private boolean useRemaining;

        public EntityDrinkBloodEvent(@NotNull IVampire vampire, int amount, float saturation, boolean useRemaining, EnumBloodSource bloodSource) {
            super(vampire, amount, saturation, bloodSource);
            this.useRemaining = useRemaining;
        }
        /**
         * @return Whether the remaining blood should be used, see {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean, de.teamlapen.vampirism.api.entity.player.vampire.EnumBloodSource)}.
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
