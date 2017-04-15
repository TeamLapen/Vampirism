package de.teamlapen.vampirism.modcompat.guide;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;


public class GuideAPICompat implements IModCompat {
    @Override
    public String getModID() {
        return "guideapi";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }


    @Override
    public void onInitStep(Step step, FMLStateEvent event) {

    }
}
