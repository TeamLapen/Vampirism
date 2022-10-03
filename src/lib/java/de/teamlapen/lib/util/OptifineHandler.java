package de.teamlapen.lib.util;

import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility for dealing with Optifine
 */
public class OptifineHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean isLoaded = false;
    private static boolean didCheck = false;
    @Nullable
    private static Method method_isShaders;


    public static boolean isOptifineLoaded() {
        if (didCheck) return isLoaded;
        if (!FMLEnvironment.dist.isClient()) { //Only check on client side
            isLoaded = false;
            method_isShaders = null;
            didCheck = true;
            return false;
        }
        try {
            Class<?> configClass = Class.forName("net.optifine.Config");
            isLoaded = true; //If no exception is thrown the class is present, so we expect Optifine to be active
            try {
                method_isShaders = configClass.getDeclaredMethod("isShaders");
            } catch (NoSuchMethodException e) {
                LOGGER.error("Could not retrieve shader check method from Optifine config", e);
            }

        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {

        }
        didCheck = true;
        return isLoaded;
    }

    public static boolean isShaders() {
        try {
            return method_isShaders != null && (Boolean) method_isShaders.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return false;
    }


}
