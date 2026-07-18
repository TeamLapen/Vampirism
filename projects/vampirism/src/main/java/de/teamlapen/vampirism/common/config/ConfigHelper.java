package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.api.VReference;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ConfigHelper {

    private int darkStalkerTicksPerBlood;

    public int getDarkStalkerTicksPerBlood() {
        return this.darkStalkerTicksPerBlood;
    }

    void onBalanceConfigChanged(ModConfigEvent event) {
        double darkStalkerBloodConsumption = ModConfig.balance().vaDarkStalkerBloodConsumption.getAsDouble();
        this.darkStalkerTicksPerBlood = (int) (VReference.FOOD_TO_FLUID_BLOOD / darkStalkerBloodConsumption);
    }
}