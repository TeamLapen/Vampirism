package de.teamlapen.vampirism.modcompat.bop;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Biomes o Plenty Compat
 */
public class BoPModCompat implements IModCompat {

    boolean disabled_sundamage_ominous_woods;

    @Override
    public String getModID() {
        return "biomesoplenty";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {
        disabled_sundamage_ominous_woods = config.getBoolean("disable_sundamage_ominous_woods", category.getName(), true, "Whether sundamage should be applied to vampires in this biome or not");
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        if (step == Step.POST_INIT) {
            BoPBiomes.registerNoSundamageBiomes(this);
        }
    }
}
