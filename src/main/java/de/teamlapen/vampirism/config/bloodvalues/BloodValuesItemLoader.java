package de.teamlapen.vampirism.config.bloodvalues;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @deprecated use {@link BloodValueReader}. This class exists only for backwards compatibility.
 */
@Deprecated //TODO remove
public class BloodValuesItemLoader extends BloodValueReader<ResourceLocation> {

    public BloodValuesItemLoader(Consumer<Map<ResourceLocation, Float>> valueConsumer, String directory, String name) {
        super(valueConsumer, directory, name);
    }

    @Override
    public Map<String, BloodValueBuilder> loadValues(IResourceManager manager) {
        Map<String, BloodValueBuilder> values = super.loadValues(manager);
        loadLegacy(manager, values);
        return values;
    }

    private void loadLegacy(IResourceManager manager, Map<String, BloodValueBuilder> values) {
        BiConsumer<Map<ResourceLocation, Integer>, Integer> consumer = (map, multiplier) ->{
            map.forEach((loc, value) -> {
                values.computeIfAbsent(loc.getNamespace(), (id1) -> new BloodValueBuilder()).addValue(loc, value * multiplier, "legacy");
            });
        };
        BloodValueLoader legacyLoader = new BloodValueLoader("items", consumer, new ResourceLocation("multiplier"));
        Collection<ResourceLocation> locs = legacyLoader.prepare(manager, null);
        legacyLoader.apply(locs, manager, null);
    }
}
