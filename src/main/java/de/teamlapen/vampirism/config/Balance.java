package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.vampirism.VampirismMod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Main balance configuration handler
 */
public class Balance {
    private final static String TAG = "Balance";
    private final static Map<String, BalanceValues> categories = new HashMap<String, BalanceValues>();
    public static BalanceLeveling leveling;
    public static BalanceMobProps mobProps;
    public static BalanceVampirePlayer vp;
    public static BalanceHunterPlayer hp;
    public static BalanceVampireSkills vps;
    public static BalanceGeneral general;

    public static void init(File configDir, boolean inDev) {
        File balanceDir = new File(configDir, "balance");
        leveling = new BalanceLeveling(balanceDir);
        mobProps = new BalanceMobProps(balanceDir);
        vp = new BalanceVampirePlayer(balanceDir);
        hp = new BalanceHunterPlayer(balanceDir);
        vps = new BalanceVampireSkills(balanceDir);
        general = new BalanceGeneral(balanceDir);
        categories.put(leveling.getName(), leveling);
        categories.put(mobProps.getName(), mobProps);
        categories.put(vp.getName(), vp);
        categories.put(hp.getName(), hp);
        categories.put(vps.getName(), vps);
        categories.put(general.getName(), general);
        if (inDev && Configs.resetConfigurationInDev) {
            reset(null);
        }
        loadConfiguration();
        VampirismMod.log.i(TAG, "Loaded balance configuration");
    }

    private static void loadConfiguration() {
        for (BalanceValues values : categories.values()) {
            values.loadBalance();
        }
    }

    public static void onConfigurationChanged() {
        VampirismMod.log.i(TAG, "Reloading changed balance configuration");
        loadConfiguration();
    }

    /**
     * Resets the matching balance category.
     *
     * @param category False if category is not found
     * @return
     */
    public static boolean reset(String category) {
        if (category == null || category.equals("all")) {
            for (BalanceValues values : categories.values()) {
                values.reset();
            }
            return true;
        }
        BalanceValues values = categories.get(category);
        if (values != null) {
            values.reset();
            return true;
        }
        return false;
    }

    public static Map<String, BalanceValues> getCategories() {
        return categories;
    }

}
