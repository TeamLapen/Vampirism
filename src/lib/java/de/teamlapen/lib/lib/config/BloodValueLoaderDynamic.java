package de.teamlapen.lib.lib.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.lib.lib.util.ResourceLocationTypeAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated //TODO remove
public class BloodValueLoaderDynamic extends BloodValueLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<BloodValueLoaderDynamic> LOADER = Lists.newArrayList();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(new TypeToken<ResourceLocation>() {
    }.getType(), new ResourceLocationTypeAdapter()).create();

    private static void writeBloodValues(Writer w, Map<ResourceLocation, Integer> values, String comment) throws IOException, JsonIOException {
        try (BufferedWriter bw = new BufferedWriter(w)) {
            bw.write('#');
            bw.write(comment);
            bw.newLine();
            bw.write(GSON.toJson(values));
            bw.flush();
        }
    }

    private static Optional<Map<ResourceLocation, Integer>> loadBloodValues(Reader r) throws IOException, JsonSyntaxException {
        try (BufferedReader br = new BufferedReader(r)) {
            br.readLine();
            Type s = new TypeToken<Map<ResourceLocation, Integer>>() {
            }.getType();
            return Optional.ofNullable(GSON.fromJson(br, s));
        }
    }

    public static List<BloodValueLoaderDynamic> getDynamicBloodLoader() {
        return ImmutableList.copyOf(LOADER);
    }

    private final Consumer<Map<ResourceLocation, Integer>> addCalculatedValues;
    private final Supplier<Map<ResourceLocation, Integer>> getCalculatedValues;
    private final String name;
    @SuppressWarnings("FieldCanBeLocal")
    private final String modId;
    private final LevelResource worldSubFolder;
    /**
     * File to save dynamically calculated values to
     */
    @Nullable
    private File bloodValueWorldFile;

    public BloodValueLoaderDynamic(@NotNull String modIdIn, @NotNull String nameIn, @NotNull BiConsumer<Map<ResourceLocation, Integer>, Integer> consumerIn, @Nullable ResourceLocation multiplierNameIn, @NotNull Consumer<Map<ResourceLocation, Integer>> addCalculatedValuesIn, @NotNull Supplier<Map<ResourceLocation, Integer>> getCalculatedValuesIn) {
        super(nameIn, consumerIn, multiplierNameIn);
        this.addCalculatedValues = addCalculatedValuesIn;
        this.getCalculatedValues = getCalculatedValuesIn;
        this.name = nameIn;
        this.modId = modIdIn;
        this.worldSubFolder = new LevelResource(modId);
        LOADER.add(this);
    }

    public void onServerStarting(MinecraftServer server) {
        bloodValueWorldFile = new File(server.getWorldPath(worldSubFolder).toFile(), "calculated-" + name + "-blood-values.txt");
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
            Optional<Map<ResourceLocation, Integer>> saved = loadBloodValues(new InputStreamReader(new FileInputStream(f)));
            saved.ifPresent(this.addCalculatedValues);
        } catch (IOException | JsonIOException e) {
            LOGGER.error(LogUtil.CONFIG, "Could not read saved " + name + " blood values from world from file " + f, e);
        }
    }

    /**
     * Saves blood values to file to be saved in world dir
     */
    private void saveDynamicBloodValues(File f) {
        Map<ResourceLocation, Integer> values = this.getCalculatedValues.get();
        if (!f.exists() && values.isEmpty()) return; //Don't create an empty file
        if (!f.exists()) {
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
        }
        try {
            writeBloodValues(new FileWriter(f), values, "Dynamically calculated blood values - DON'T EDIT");
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.warn(LogUtil.CONFIG, "Could not write calculated " + name + " values to file", e);
        }
    }
}
