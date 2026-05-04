package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.api.VReference;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ConfigHelper {

    private int darkStalkerTicksPerBlood;
    private int garlicFinderAuraColor;

    public int getDarkStalkerTicksPerBlood() {
        return this.darkStalkerTicksPerBlood;
    }

    public int getGarlicFinderAuraColor() {
        return garlicFinderAuraColor;
    }

    void onBalanceConfigChanged(ModConfigEvent event) {
        double darkStalkerBloodConsumption = ModConfig.balance().vaDarkStalkerBloodConsumption.getAsDouble();
        this.darkStalkerTicksPerBlood = (int) (VReference.FOOD_TO_FLUID_BLOOD / darkStalkerBloodConsumption);
    }

    void onClientConfigChanged(ModConfigEvent event) {
        String colorStr = ModConfig.client().garlicFinderAuraColor.get();
        try {
            this.garlicFinderAuraColor = Integer.parseInt(colorStr.replaceFirst("^#", ""), 16);
        } catch (NumberFormatException ignored) {
            this.garlicFinderAuraColor = 0xe0b74f;
        }
    }
}
