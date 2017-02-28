package de.teamlapen.vampirism.modcompat;

import de.teamlapen.lib.lib.util.IModCompat;
import de.teamlapen.vampirism.config.Configs;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * 1.10
 *
 * @author maxanier
 */
public class GrimoireOfGaiaModCompat implements IModCompat {
    public static final String MODID = "grimoireofgaia";

    @Override
    public String getModID() {
        return MODID;
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        if (step == Step.PRE_INIT) {
            Configs.loadBloodValuesModCompat(MODID);
        }
    }

}
