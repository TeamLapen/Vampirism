package de.teamlapen.lib.lib.config;

import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.LogUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class BloodValueLoader extends SimplePreparableReloadListener<Collection<ResourceLocation>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BLOOD_VALUE_DIRECTORY = "vampirism_blood_values/";

    private final String folderLocation;
    private final String type;
    private final BiConsumer<Map<ResourceLocation, Integer>, Integer> consumer;
    @Nullable
    private final ResourceLocation multiplierName;
    private int multiplier;

    /**
     * @param nameIn           name for data path folder inside 'vampirism_blood_value' with blood value files
     * @param consumerIn       the consumer which gets the ResourceLocation to Integer Map from the files
     * @param multiplierNameIn the ResourceLocation which declares a multiplier in data pack
     */
    public BloodValueLoader(@Nonnull String nameIn, @Nonnull BiConsumer<Map<ResourceLocation, Integer>, Integer> consumerIn, @Nullable ResourceLocation multiplierNameIn) {
        this.folderLocation = BLOOD_VALUE_DIRECTORY + nameIn;
        this.consumer = consumerIn;
        this.multiplierName = multiplierNameIn;
        this.type = nameIn;
    }

    @Override
    protected void apply(@Nonnull Collection<ResourceLocation> splashList, @Nonnull ResourceManager resourceManagerIn, @Nonnull ProfilerFiller profilerIn) {
        Map<ResourceLocation, Integer> values = Maps.newConcurrentMap();
        for (ResourceLocation location : splashList) {
            String modId = location.getPath().substring(folderLocation.length() + 1, location.getPath().length() - 4);
            Map<ResourceLocation, Integer> values_tmp = loadBloodValuesFromDataPack(location, modId, resourceManagerIn);
            if (values_tmp != null) {
                values.putAll(values_tmp);
                LOGGER.debug(LogUtil.CONFIG, "Loaded {} {} blood values from {}", values_tmp.size(), this.type, modId);
            }
        }
        LOGGER.info(LogUtil.CONFIG, "Loaded {} {} blood values", values.size(), this.type);
        consumer.accept(values, multiplier != 0 ? multiplier : 1);
    }

    /**
     * Reads blood values for a mod from the datapack.
     *
     * @param modId should be equals to the file name of location
     */
    @Nullable
    protected Map<ResourceLocation, Integer> loadBloodValuesFromDataPack(ResourceLocation location, String modId, ResourceManager resourceManager) {
        if (!ModList.get().isLoaded(modId)) return null;
        return resourceManager.getResource(location).map(resource -> {
            try (Reader reader = resource.openAsReader()) {
                return loadBloodValuesFromReader(reader, modId);
            } catch (IOException e) {
                LOGGER.error(LogUtil.CONFIG, "[ModCompat]Could not read default blood values for mod {}, this should not happen {}", modId, e);
                return null;
            }
        }).orElse(null);
    }

    /**
     * @param r     Reader the values should be read from
     * @param modId Just for logging of errors
     */
    protected <T> Map<ResourceLocation, Integer> loadBloodValuesFromReader(Reader r, String modId) throws IOException {
        Map<ResourceLocation, Integer> bloodValues = Maps.newConcurrentMap();
        try (BufferedReader br = new BufferedReader(r)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (StringUtils.isBlank(line)) continue;
                String[] p = line.split("=");
                if (p.length != 2) {
                    LOGGER.warn(LogUtil.CONFIG, "Line {}  in {} is not formatted properly", line, modId + ".txt");
                    continue;
                }
                if (p[0].equals(modId)) {
                    LOGGER.warn(LogUtil.CONFIG, "{} is not applicant for other the mod {} in {}", line, modId, modId + ".txt");
                }
                int val;
                try {
                    val = Integer.parseInt(p[1]);
                } catch (NumberFormatException e) {
                    LOGGER.warn(LogUtil.CONFIG, "Line {}  in {} is not formatted properly", line, modId + ".txt");
                    continue;
                }
                try {
                    ResourceLocation resourceLocation = new ResourceLocation(p[0]);
                    if (!resourceLocation.getNamespace().equals(modId)) {
                        LOGGER.warn(LogUtil.CONFIG, "Wrong namespace for entry {} in {}", p[0], modId + ".txt");
                    } else {
                        if (resourceLocation.equals(multiplierName)) {
                            multiplier = val;
                        } else if (bloodValues.put(resourceLocation, val) != null) {
                            LOGGER.warn(LogUtil.CONFIG, "Duplicated entry for {} is being overridden in {}", modId + ".txt", p[0]);
                        }
                    }
                } catch (ResourceLocationException e) {
                    LOGGER.error(LogUtil.CONFIG,"Id is not valid in file "+modId +".txt"+": " + line , e);
                }

            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to load blood values from reader " + modId, e);
        }
        return bloodValues;

    }

    @Nonnull
    @Override
    protected Collection<ResourceLocation> prepare(ResourceManager resourceManagerIn, @Nonnull ProfilerFiller profilerIn) {
        return resourceManagerIn.listResources(folderLocation, (file) -> file.getPath().endsWith(".txt")).keySet();
    }
}
