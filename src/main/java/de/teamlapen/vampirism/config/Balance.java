package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BalanceValues;
import de.teamlapen.lib.lib.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Main balance configuration handler
 */
public class Balance {
    private final static Logger LOGGER = LogManager.getLogger();

    private final static Map<String, BalanceValues> categories = new HashMap<>();
    public static BalanceMobProps mobProps;
    public static BalanceVampirePlayer vp;
    public static BalanceVampireActions vpa;

    public static void init(File configDir, boolean inDev) {
        File balanceDir = new File(configDir, "balance");
        mobProps = addBalance(new BalanceMobProps(balanceDir));
        vp = addBalance(new BalanceVampirePlayer(balanceDir));
        vpa = addBalance(new BalanceVampireActions(balanceDir));
        if (inDev) {
            resetAndReload(null);
        } else {
            loadConfiguration();
        }

        LOGGER.info(LogUtil.CONFIG, "Loaded balance configuration");
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
        LOGGER.info(LogUtil.CONFIG, "Reloading changed balance configuration");
        loadConfiguration();
    }


    /**
     * Resets the matching balance category and reloads it
     *
     * @param category False if category is not found
     * @return
     */
    public static boolean resetAndReload(@Nullable String category) {
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
