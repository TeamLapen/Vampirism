package de.teamlapen.lib.lib.config;

import de.teamlapen.lib.VampLib;
import net.minecraftforge.common.config.Configuration;

import java.io.PrintWriter;

/**
 * Contains helper classes for configuration
 */
public class ConfigHelper {

    /**
     * Resets the configuration by simply clearing the config file
     * @param config
     * @return
     */
    public static Configuration reset(Configuration config){
        VampLib.log.i("Configs", "Resetting config file " + config.getConfigFile().getName());
        try {
            PrintWriter writer = new PrintWriter(config.getConfigFile());
            writer.write("");
            writer.flush();
            writer.close();
            return new Configuration(config.getConfigFile());
        } catch (Exception e) {
            VampLib.log.e("Configs", "Failed to reset config file");
        }
        return config;
    }
}
