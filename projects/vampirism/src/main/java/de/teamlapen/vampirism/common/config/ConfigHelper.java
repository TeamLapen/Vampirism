package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.api.VReference;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;

public class ConfigHelper {

    private int ticksPerBlood;
    private int garlicFinderAuraColor;

    public int getTicksPerBlood() {
        return this.ticksPerBlood;
    }

    public int getGarlicFinderAuraColor() {
        return garlicFinderAuraColor;
    }

    void onBalanceConfigChanged(@NotNull ModConfigEvent event) {
        double asDouble = ModConfig.balance().vaDarkStalkerBloodConsumption.getAsDouble();
        this.ticksPerBlood = (int) (VReference.FOOD_TO_FLUID_BLOOD / asDouble);
    }

    void onClientConfigChanged(@NotNull ModConfigEvent event) {
        String colorStr = ModConfig.client().garlicFinderAuraColor.get();
        try {
            this.garlicFinderAuraColor = Integer.parseInt(colorStr.replaceFirst("^#", ""), 16);
        } catch (NumberFormatException ignored) {
            this.garlicFinderAuraColor = 0xe0b74f;
        }
    }
}
