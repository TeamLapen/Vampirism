package de.teamlapen.vampirism.modcompat;

import de.teamlapen.lib.lib.config.forge.ConfigCategory;
import de.teamlapen.lib.lib.config.forge.Configuration;
import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;


public class SpongeModCompat implements IModCompat {
    public static final String MODID = "sponge";

    @Override
    public String getModID() {
        return MODID;
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {

    }
}
