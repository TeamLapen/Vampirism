package de.teamlapen.vampirism.modcompat;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;


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
    public void onInitStep(Step step, FMLStateEvent event) {

    }
}
