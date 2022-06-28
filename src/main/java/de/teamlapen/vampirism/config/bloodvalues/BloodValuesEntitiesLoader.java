package de.teamlapen.vampirism.config.bloodvalues;

import de.teamlapen.lib.lib.config.BloodValueLoader;
import de.teamlapen.vampirism.REFERENCE;
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
public class BloodValuesEntitiesLoader extends BloodValueReader<ResourceLocation> {

    public BloodValuesEntitiesLoader(Consumer<Map<ResourceLocation, Float>> valueConsumer, String directory, String name) {
        super(valueConsumer, directory, name);
    }

    @Override
    public Map<ResourceLocation, BloodValueBuilder> loadValues(IResourceManager manager) {
        Map<ResourceLocation, BloodValueBuilder> values = super.loadValues(manager);
        loadLegacy(manager, values);
        return values;
    }

    private void loadLegacy(IResourceManager manager,Map<ResourceLocation, BloodValueBuilder> values) {
        BiConsumer<Map<ResourceLocation, Integer>, Integer> consumer = (map, multiplier) ->{
            map.forEach((loc, value) -> {
                ResourceLocation id = new ResourceLocation(REFERENCE.MODID, loc.getNamespace());
                values.computeIfAbsent(id, (id1) -> new BloodValueBuilder()).addValue(loc, (float)value * multiplier, "legacy");
            });
        };
        BloodValueLoader legacyLoader = new BloodValueLoader("entities", consumer, new ResourceLocation("multiplier"));
        Collection<ResourceLocation> locs = legacyLoader.prepare(manager, null);
        legacyLoader.apply(locs, manager, null);
    }
}
