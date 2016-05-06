package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * JEI automatically detects the plugin class so nothing to do here
 */
public class JEIModCompat implements IModCompat {
    @Override
    public String getModID() {
        return "JEI";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {

    }
}
