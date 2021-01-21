package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.BalanceConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;


public class ConfigurableStructureSeparationSettings extends StructureSeparationSettings {

    private final ForgeConfigSpec.IntValue distanceConf;
    private final ForgeConfigSpec.IntValue separationConf;


    public ConfigurableStructureSeparationSettings(ForgeConfigSpec.IntValue distanceConf, ForgeConfigSpec.IntValue separationConf, int salt) {
        super(distanceConf.get(), separationConf.get(), salt); //At this point the config is probably not loaded yet
        this.distanceConf = distanceConf;
        this.separationConf = separationConf;
    }

    @Override
    public int func_236668_a_() {
        int dist = distanceConf.get();
        if (dist <= func_236671_b_()) {
            LogManager.getLogger(BalanceConfig.class).warn("config value 'distance' must be greater than 'separation'. 'distance' increased");
            dist = func_236671_b_() + 1;
        }
        return dist;
    }

    @Override
    public int func_236671_b_() {
        return separationConf.get();
    }
}
