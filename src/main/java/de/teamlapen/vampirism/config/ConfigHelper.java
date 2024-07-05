package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.api.VReference;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;

public class ConfigHelper {

    private int ticksPerBlood;

    public int getTicksPerBlood() {
        return this.ticksPerBlood;
    }

    void onBalanceConfigChanged(@NotNull ModConfigEvent event) {
        double asDouble = VampirismConfig.BALANCE.vaDarkStalkerBloodConsumption.getAsDouble();
        this.ticksPerBlood = (int) (VReference.FOOD_TO_FLUID_BLOOD / asDouble);
    }
}
