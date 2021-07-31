package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.BalanceConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;


public class ConfigurableStructureSeparationSettings extends StructureSeparationSettings {

    private final ForgeConfigSpec.IntValue distanceConf;
    private final ForgeConfigSpec.IntValue separationConf;


    /**
     * @param distanceConf   Maximum distance between spawn attempts in chunks
     * @param separationConf Minimum distance between spawn attempts in chunks
     * @param salt           Must be Unique
     */
    public ConfigurableStructureSeparationSettings(ForgeConfigSpec.IntValue distanceConf, ForgeConfigSpec.IntValue separationConf, int salt) {
        super(distanceConf.get() <= separationConf.get() ? separationConf.get() + 1 : distanceConf.get(), separationConf.get(), salt); //At this point the config is probably not loaded yet
        this.distanceConf = distanceConf;
        this.separationConf = separationConf;
    }

    @Override
    public int separation() {
        return separationConf.get();
    }

    @Override
    public int spacing() {
        int dist = distanceConf.get();
        if (dist <= separation()) {
            LogManager.getLogger(BalanceConfig.class).warn("config value 'distance' must be greater than 'separation'. 'distance' increased");
            dist = separation() + 1;
        }
        return dist;
    }
}
