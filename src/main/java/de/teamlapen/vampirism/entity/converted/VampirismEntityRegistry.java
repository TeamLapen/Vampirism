package de.teamlapen.vampirism.entity.converted;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.datamaps.ConverterEntry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VampirismEntityRegistry implements IVampirismEntityRegistry {

    @NotNull
    public final BiteableEntryManager biteableEntryManager = new BiteableEntryManager();

    @Override
    @Nullable
    public IConvertedCreature<?> convert(@NotNull PathfinderMob entity) {
        Holder<EntityType<?>> holder = entity.getType().builtInRegistryHolder();
        return Optional.ofNullable(holder.getData(VampirismRegistries.ENTITY_BLOOD_MAP.get())).filter(s -> s.blood() > 0).map(a -> holder.getData(VampirismRegistries.ENTITY_CONVERTER_MAP.get())).map(s -> s.converter().createHandler(s.overlay().orElse(null))).map(s -> ((IConvertingHandler<PathfinderMob>)s).createFrom(entity)).orElse(null);
    }

    @NotNull
    public Map<EntityType<?>, ResourceLocation> getConvertibleOverlay() {
        DefaultedRegistry<EntityType<?>> registry = BuiltInRegistries.ENTITY_TYPE;
        //noinspection unchecked
        return Map.ofEntries(registry.getDataMap(ModRegistries.ENTITY_CONVERTER_MAP).entrySet().stream().flatMap(s -> s.getValue().overlay().flatMap(l -> Optional.ofNullable(registry.get(s.getKey())).map(p -> Map.entry(p, l))).stream()).toArray(Map.Entry[]::new));
    }

    @Override
    public @Nullable ResourceLocation getConvertibleOverlay(String originalEntity) {
        return BuiltInRegistries.ENTITY_TYPE.getHolder(ResourceKey.create(Registries.ENTITY_TYPE, new ResourceLocation(originalEntity))).map(s -> s.getData(VampirismRegistries.ENTITY_CONVERTER_MAP.get())).flatMap(IConverterEntry::overlay).orElse(null);
    }

    @Override
    public @Nullable IEntityBlood getEntry(@NotNull PathfinderMob creature) {
        return this.biteableEntryManager.get(creature);
    }

    @Override
    public @Nullable IConverterEntry getConverterEntry(@NotNull PathfinderMob creature) {
        return creature.getType().builtInRegistryHolder().getData(ModRegistries.ENTITY_CONVERTER_MAP);
    }

    @Override
    public @NotNull IEntityBlood getOrCreateEntry(PathfinderMob creature) {
        return this.biteableEntryManager.getOrCalculate(creature);
    }

    public record DefaultHelper(ConverterEntry.ConvertingAttributeModifier attributes) implements IConvertingHandler.IDefaultHelper {

        @Override
        public Map<Attribute, Pair<FloatProvider, Double>> getAttributeModifier() {
            return this.attributes.attributeModifier();
        }
    }
}