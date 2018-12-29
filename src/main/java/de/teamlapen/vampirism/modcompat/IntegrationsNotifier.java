package de.teamlapen.vampirism.modcompat;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntegrationsNotifier {

    private final static String[] available_compats = new String[]{"abyssalcraft", "biomesoplenty", "mca", "toroquest", "waila", "evilcraft", "tconstruct", "bloodmagic", "toughasnails"};

    /**
     * Check if there should be a notification about the integrations mods.
     * Checks if it is installed, if there are any mods with potential compat installed and if there already was a notification about these mods
     * Only call after pre-init
     *
     * @return Empty list if no notification. Otherwise list of installed mod ids with potential compat
     */
    public static List<String> shouldNotifyAboutIntegrations() {
        if (!Loader.isModLoaded(REFERENCE.INTEGRATIONS_MODID)) {
            List<String> installedMods = Lists.newArrayList();
            for (String s : available_compats) {
                if (Loader.isModLoaded(s)) {
                    installedMods.add(s);
                }
            }
            if (installedMods.size() > 0) {
                if (!checkAndUpdateAlreadyNotified(installedMods)) {
                    return installedMods;
                }
            }

        }
        return Collections.emptyList();
    }

    /**
     * Check if a notification regarding all the given mods has been displayed already.
     * If not add the given mods to the already notified mod list
     *
     * @param mods
     * @return If already notified
     */
    private static boolean checkAndUpdateAlreadyNotified(List<String> mods) {
        Configuration config = VampirismMod.instance.modCompatLoader.getConfig();
        assert config != null : "Do not call before init";
        Property conf = config.get(Configuration.CATEGORY_GENERAL, "integration_mod_notification", "", "INTERNAL - Set to 'never' if you don't want to be notified about integration mods");
        String saved = conf.getString();
        if ("never".equals(saved) || "'never'".equals(saved)) {
            return true;
        }
        String[] previous = saved.split(":");
        List<String> missing = new ArrayList<>(mods);
        missing.removeAll(Arrays.asList(previous));
        if (missing.size() == 0) {
            return true;
        }
        Collections.addAll(missing, previous);
        conf.set(StringUtils.join(missing, ":"));
        config.save();
        return false;
    }
}
