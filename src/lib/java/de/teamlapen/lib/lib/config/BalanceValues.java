package de.teamlapen.lib.lib.config;

import de.teamlapen.lib.lib.config.forge.ConfigCategory;
import de.teamlapen.lib.lib.config.forge.Configuration;
import de.teamlapen.lib.lib.util.LogUtil;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Can be used to easily create config values by just adding a simply variable with an annotation containing the default value.
 *
 * @author Maxanier
 */
public abstract class BalanceValues {
    private static final Logger LOGGER = LogManager.getLogger("TestLogger");


    /**
     * Resets the configuration by simply clearing the config file
     *
     * @param f File to reset
     * @return
     */
    private static void reset(File f) {
        //TODO 1.13
//        LOGGER.info(LogUtil.CONFIG, "Resetting config file " + f.getName());
//        try {
//            PrintWriter writer = new PrintWriter(f);
//            writer.write("");
//            writer.flush();
//            writer.close();
//        } catch (Exception e) {
//            LOGGER.error(LogUtil.CONFIG, "Failed to reset config file");
//        }
    }

    private final String name;
    private Configuration configuration;

    /**
     * Creates a configuration for balance values
     *
     * @param name
     * @param directory
     */
    public BalanceValues(String name, File directory) {
        configuration = new Configuration(new File(directory, name + ".cfg"));
        this.name = name;

    }

    public ConfigCategory getConfigCategory() {
        return configuration.getCategory(Configuration.CATEGORY_GENERAL);
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    public void loadBalance() {
        ConfigCategory cat = configuration.getCategory(Configuration.CATEGORY_GENERAL);
        boolean alt = shouldUseAlternate();
        for (Field f : this.getClass().getDeclaredFields()) {
            String name = f.getName();
            Class type = f.getType();
            try {
                if (type == int.class) {
                    DefaultInt a = f.getAnnotation(DefaultInt.class);
                    int value = (alt && a.hasAlternate()) ? a.alternateValue() : a.value();
                    f.set(this, MathHelper.clamp(configuration.get(cat.getQualifiedName(), chooseName(name, a.name()), value, a.comment(), a.minValue(), a.maxValue()).getInt(), a.minValue(), a.maxValue()));
                } else if (type == double.class) {
                    // Possible exception should not be caught so you can't forget a default value
                    DefaultDouble a = f.getAnnotation(DefaultDouble.class);
                    double value = (alt && a.hasAlternate()) ? a.alternateValue() : a.value();
                    f.set(this, MathHelper.clamp(configuration.get(cat.getQualifiedName(), chooseName(name, a.name()), value, a.comment(), a.minValue(), a.maxValue()).getDouble(), a.minValue(), a.maxValue()));
                } else if (type == boolean.class) {
                    DefaultBoolean a = f.getAnnotation(DefaultBoolean.class);
                    boolean value = (alt && a.hasAlternate()) ? a.alternateValue() : a.value();
                    f.set(this, configuration.get(cat.getQualifiedName(), chooseName(name, a.name()), value, a.comment()).getBoolean());
                }
            } catch (NullPointerException e1) {
                LOGGER.error(LogUtil.CONFIG, "Author probably forgot to specify a default annotation for " + name + " in " + this.name, e1);
                throw new Error("Please check you default values in " + this.name);
            } catch (Exception e) {
                LOGGER.error(LogUtil.CONFIG, "Cant set " + this.name + " values", e);
                throw new Error("Please check your " + configuration.getConfigFile().getAbsolutePath() + " config file");
            }
        }
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    /**
     * Firstly clears the config file, then replaces the old configuration by a new one so the new (emtpy) file is loaded and then loads the default values and saves them.
     */
    public void resetAndReload() {
        reset(configuration.getConfigFile());
        configuration = new Configuration(configuration.getConfigFile());
        loadBalance();
    }

    /**
     * @return Wether to use alternative default values if present or not
     */
    protected abstract boolean shouldUseAlternate();

    private String chooseName(String fieldName, String attrName) {
        if (attrName.isEmpty()) {
            return fieldName.toLowerCase();
        }
        return attrName;
    }
}
