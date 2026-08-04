package de.teamlapen.vampirism.client;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

/**
 * Utility for dealing with Iris and Optifine
 */
public class ShaderHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean didCheck = false;
    private static boolean optifineLoaded = false;
    private static boolean irisLoaded = false;
    @Nullable
    private static BooleanSupplier optifineShaders;
    @Nullable
    private static BooleanSupplier irisShaders;

    public static boolean isOptifineLoaded() {
        check();
        return optifineLoaded;
    }

    public static boolean isIrisLoaded() {
        check();
        return irisLoaded;
    }

    public static boolean areOptifineShadersActive() {
        check();
        return isActive(optifineShaders);
    }

    /**
     * @return Whether any shader pack is currently rendering
     */
    public static boolean areShadersActive() {
        check();
        return isActive(optifineShaders) || isActive(irisShaders);
    }

    private static void check() {
        if (didCheck) return;
        didCheck = true;

        if (!FMLEnvironment.getDist().isClient()) return; // Only check on client side

        resolveOptifine();
        resolveIris();
    }

    private static void resolveOptifine() {
        Class<?> configClass;
        try {
            configClass = Class.forName("net.optifine.Config");
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            return;
        }
        optifineLoaded = true; // If no exception is thrown the class is present, so we expect Optifine to be active
        try {
            Method isShaders = configClass.getDeclaredMethod("isShaders");
            optifineShaders = () -> invoke(isShaders, null);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Could not retrieve shader check method from Optifine config", e);
        }
    }

    private static void resolveIris() {
        if (!ModList.get().isLoaded("iris")) return;

        irisLoaded = true;
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            Method isShaderPackInUse = apiClass.getMethod("isShaderPackInUse");
            irisShaders = () -> invoke(isShaderPackInUse, api);
        } catch (ReflectiveOperationException | NoClassDefFoundError e) {
            LOGGER.error("Could not retrieve the shader check method from the Iris api", e);
        }
    }

    private static boolean isActive(@Nullable BooleanSupplier shaders) {
        return shaders != null && shaders.getAsBoolean();
    }

    private static boolean invoke(Method method, @Nullable Object instance) {
        try {
            return (Boolean) method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException ignored) {
        }
        return false;
    }
}