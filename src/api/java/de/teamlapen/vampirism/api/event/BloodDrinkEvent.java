package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public abstract class BloodDrinkEvent extends Event {
    @NotNull
    private final IVampire vampire;
    private int amount;
    private float saturation;
    private final IDrinkBloodContext bloodSource;


    private BloodDrinkEvent(@NotNull IVampire vampire, int amount, float saturation, IDrinkBloodContext bloodSource) {
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
    public IDrinkBloodContext getBloodSource() {
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
     * Posted whenever a Vampire Player drinks blood, such as when drinking blood from a bottle, eating a human heart or biting another mob
     */
    public static class PlayerDrinkBloodEvent extends BloodDrinkEvent {
        private boolean useRemaining;

        public PlayerDrinkBloodEvent(@NotNull IVampirePlayer player, int amount, float saturation, boolean useRemaining, IDrinkBloodContext bloodSource) {
            super(player, amount, saturation, bloodSource);
            this.useRemaining = useRemaining;
        }
        /**
         * @return Whether the remaining blood should be used, see {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean, de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext)}.
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

        public EntityDrinkBloodEvent(@NotNull IVampire vampire, int amount, float saturation, boolean useRemaining, IDrinkBloodContext bloodSource) {
            super(vampire, amount, saturation, bloodSource);
            this.useRemaining = useRemaining;
        }
        /**
         * @return Whether the remaining blood should be used, see {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean, de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext)}.
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
