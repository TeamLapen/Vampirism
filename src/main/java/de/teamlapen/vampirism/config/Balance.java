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
    public static BalanceVampireActions vpa;
    public static BalanceVampireSkills vps;
    public static BalanceGeneral general;
    public static BalanceVillage village;
    public static BalanceHunterSkills hps;
    public static BalanceHunterActions hpa;

    public static void init(File configDir, boolean inDev) {
        File balanceDir = new File(configDir, "balance");
        leveling = addBalance(new BalanceLeveling(balanceDir));
        mobProps = addBalance(new BalanceMobProps(balanceDir));
        vp = addBalance(new BalanceVampirePlayer(balanceDir));
        hp = addBalance(new BalanceHunterPlayer(balanceDir));
        vpa = addBalance(new BalanceVampireActions(balanceDir));
        general = addBalance(new BalanceGeneral(balanceDir));
        vps = addBalance(new BalanceVampireSkills(balanceDir));
        village = addBalance(new BalanceVillage(balanceDir));
        hps = addBalance(new BalanceHunterSkills(balanceDir));
        hpa = addBalance(new BalanceHunterActions(balanceDir));
        if (inDev && Configs.resetConfigurationInDev) {
            resetAndReload(null);
        } else {
            loadConfiguration();
        }

        VampirismMod.log.i(TAG, "Loaded balance configuration");
    }

    private static <T extends BalanceValues> T addBalance(T cat) {
        categories.put(cat.getName(), cat);
        return cat;
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
     * Resets the matching balance category and reloads it
     *
     * @param category False if category is not found
     * @return
     */
    public static boolean resetAndReload(String category) {
        if (category == null || category.equals("all")) {
            for (BalanceValues values : categories.values()) {
                values.resetAndReload();
            }
            return true;
        }
        BalanceValues values = categories.get(category);
        if (values != null) {
            values.resetAndReload();
            return true;
        }
        return false;
    }

    public static Map<String, BalanceValues> getCategories() {
        return categories;
    }

}
