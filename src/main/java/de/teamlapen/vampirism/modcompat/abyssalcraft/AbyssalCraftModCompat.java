package de.teamlapen.vampirism.modcompat.abyssalcraft;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Modcompat for AbyssalCraft
 */
public class AbyssalCraftModCompat implements IModCompat {

    boolean disableSundamage_darklands;

    @Override
    public String getModID() {
        return "abyssalcraft";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {
        disableSundamage_darklands = config.getBoolean("disabled_sundamage_darklands", category.getName(), true, "Whether to disable the sundamage in the darkland biomes or not");
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        if (step == Step.POST_INIT) {
            AbyssalCraftBiomes.registerNoSundamageBiomes(this);
        }
    }
}
