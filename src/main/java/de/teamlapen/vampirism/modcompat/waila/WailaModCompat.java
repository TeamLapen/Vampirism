package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Registers mod compatibility with waila
 */
public class WailaModCompat implements IModCompat {
    @Override
    public String getModID() {
        return "Waila";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        if (step == Step.INIT) {
            FMLInterModComms.sendMessage(getModID(), "register", WailaHandler.class.getName() + ".onRegister");
        }
    }
}
