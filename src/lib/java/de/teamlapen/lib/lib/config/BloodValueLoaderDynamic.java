package de.teamlapen.lib.lib.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.teamlapen.lib.lib.util.LogUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BloodValueLoaderDynamic extends BloodValueLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<BloodValueLoaderDynamic> LOADER = Lists.newArrayList();

    /**
     * File to save dynamically calculated values to
     */
    private @Nullable
    static File bloodValueWorldFile;
    private final Consumer<Map<ResourceLocation, Integer>> addCalculatedValues;
    private final Supplier<Map<ResourceLocation, Integer>> getCalculatedValues;
    private final String name;
    private final String modId;

    public BloodValueLoaderDynamic(@Nonnull String modIdIn, @Nonnull String nameIn, @Nonnull BiConsumer<Map<ResourceLocation, Integer>, Integer> consumerIn, @Nullable ResourceLocation multiplierNameIn, @Nonnull Consumer<Map<ResourceLocation, Integer>> addCalculatedValuesIn, @Nonnull Supplier<Map<ResourceLocation, Integer>> getCalculatedValuesIn) {
        super(nameIn, consumerIn, multiplierNameIn);
        this.addCalculatedValues = addCalculatedValuesIn;
        this.getCalculatedValues = getCalculatedValuesIn;
        this.name = nameIn;
        this.modId = modIdIn;
        LOADER.add(this);
    }

    public void onServerStarting(MinecraftServer server) {
        bloodValueWorldFile = new File(new File(server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), modId), "calculated-" + name + "-blood-values.txt");
        if (bloodValueWorldFile.exists()) {
            loadDynamicBloodValues(bloodValueWorldFile);
        }
    }

    public void onServerStopping() {
        if (bloodValueWorldFile != null) {
            saveDynamicBloodValues(bloodValueWorldFile);
        } else {
            LOGGER.warn("Can't save {} blood values. File does not exist", name);
        }

    }

    /**
     * Reads automatically calculated values from world file
     */
    private void loadDynamicBloodValues(File f) {
        try {
            Map<ResourceLocation, Integer> saved = loadBloodValuesFromReader(new InputStreamReader(new FileInputStream(f)), f.getName());
            this.addCalculatedValues.accept(saved);
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "[ModCompat]Could not read saved {} blood values from world from file {} {}", name, f, e);
        }
    }

    /**
     * Saves blood values to file to be saved in world dir
     */
    private void saveDynamicBloodValues(File f) {
        Map<ResourceLocation, Integer> values = this.getCalculatedValues.get();
        if (!f.exists() && values.isEmpty()) return; //Don't create a empty file
        if (!f.exists()) {
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
        }
        try {
            if (!writeBloodValues(new FileWriter(f), values, "Dynamically calculated blood values - DON'T EDIT")) {
                LOGGER.warn(LogUtil.CONFIG, "Could not write calculated {} values to file", name);
            }
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "Failed to write calculated blood values to file", e);
        }
    }

    private static boolean writeBloodValues(Writer w, Map<ResourceLocation, Integer> values, String comment) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(w);
            bw.write('#');
            bw.write(comment);
            bw.newLine();
            for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
                bw.write(entry.getKey().toString());
                bw.write('=');
                bw.write(String.valueOf(entry.getValue()));
                bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            LOGGER.error(LogUtil.CONFIG, "Failed to write blood values", e);
        } finally {
            if (bw != null) {
                bw.close();
            }
            w.close();
        }
        return false;
    }

    public static List<BloodValueLoaderDynamic> getDynamicBloodLoader() {
        return ImmutableList.copyOf(LOADER);
    }
}
