package de.teamlapen.lib.config;

import de.teamlapen.lib.VampLib;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Can be used to easily create config values by just adding a simply variable with an annotation containing the default value.
 * @author Maxanier
 */
public abstract class BalanceValues {
    private final static String TAG="Balance";
    private final Configuration configuration;
    private final String name;

    /**
     * Creates a configuration for balance values
     * @param name
     * @param directory
     */
    public BalanceValues(String name,File directory){
        configuration=new Configuration(new File(directory,name+".cfg"));
        this.name=name;

    }

    /**
     * @return Wether to use alternative default values if present or not
     */
    protected abstract boolean shouldUseAlternate();
    public void loadBalance(){
        ConfigCategory cat=configuration.getCategory(Configuration.CATEGORY_GENERAL);
        boolean alt=shouldUseAlternate();
        for(Field f:this.getClass().getDeclaredFields()){
            String name=f.getName();
            Class type=f.getType();
            try {
                if (type == int.class) {
                    // Possible exception should not be caught so you can't forget a default value
                    DefaultInt a = f.getAnnotation(DefaultInt.class);
                    int value=(alt&&a.hasAlternate())?a.alternateValue():a.value();
                    f.set(this, configuration.get(cat.getQualifiedName(), a.name(), value, a.comment(), a.minValue(), a.maxValue()).getInt());
                } else if (type == double.class) {
                    // Possible exception should not be caught so you can't forget a default value
                    DefaultDouble a = f.getAnnotation(DefaultDouble.class);
                    double value=(alt&&a.hasAlternate())?a.alternateValue():a.value();
                    f.set(this, configuration.get(cat.getQualifiedName(), a.name(),value, a.comment(), a.minValue(), a.maxValue()).getDouble());
                } else if (type == boolean.class) {
                    DefaultBoolean a = f.getAnnotation(DefaultBoolean.class);
                    boolean value=(alt&&a.hasAlternate())?a.alternateValue():a.value();
                    f.set(this, configuration.get(cat.getQualifiedName(), a.name(),value, a.comment()).getBoolean());
                }
            } catch (NullPointerException e1) {
                VampLib.log.e(TAG, "Author probably forgot to specify a default annotation for " + name + " in " + this.name, e1);
                throw new Error("Please check you default values in " + this.name);
            } catch (Exception e) {
                VampLib.log.e(TAG, "Cant set " + this.name + " values", e);
                throw new Error("Please check your "+configuration.getConfigFile().getAbsolutePath()+" config file");
            }
        }
        if(configuration.hasChanged()){
            configuration.save();
        }
    }

    public void reset(){
        ConfigHelper.reset(configuration);
    }

    public ConfigCategory getConfigCategory(){
        return configuration.getCategory(Configuration.CATEGORY_GENERAL);
    }

    /**
     * @return
     */
    public String getName(){
        return name;
    }
}
