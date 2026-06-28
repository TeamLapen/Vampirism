package de.teamlapen.vampirism.common.world.entity.converted;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.VampirismDataMaps;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.world.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.common.core.ModDataMaps;
import de.teamlapen.vampirism.common.datamaps.ConverterEntry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class VampirismEntityRegistry implements IVampirismEntityRegistry {

    @NotNull
    public final BiteableEntryManager biteableEntryManager = new BiteableEntryManager();

    @Override
    @Nullable
    public IConvertedCreature<?> convert(@NotNull PathfinderMob entity) {
        Holder<EntityType<?>> holder = entity.getType().builtInRegistryHolder();
        return Optional.ofNullable(holder.getData(VampirismDataMaps.ENTITY_BLOOD.get())).filter(s -> s.blood() > 0).map(a -> holder.getData(VampirismDataMaps.ENTITY_CONVERTER.get())).map(s -> s.converter().createHandler()).map(s -> ((IConvertingHandler<PathfinderMob>) s).createFrom(entity)).orElse(null);
    }

    @Override
    public @Nullable IEntityBlood getEntry(@NotNull PathfinderMob creature) {
        return this.biteableEntryManager.get(creature);
    }

    @Override
    public @Nullable IConverterEntry getConverterEntry(@NotNull PathfinderMob creature) {
        return creature.getType().builtInRegistryHolder().getData(ModDataMaps.ENTITY_CONVERTER_MAP);
    }

    @Override
    public @NotNull IEntityBlood getOrCreateEntry(PathfinderMob creature) {
        return this.biteableEntryManager.getOrCalculate(creature);
    }

    public record DefaultHelper(ConverterEntry.ConvertingAttributeModifier attributes) implements IConvertingHandler.IDefaultHelper {

        @Override
        public Map<Holder<Attribute>, Pair<FloatProvider, Double>> getAttributeModifier() {
            return this.attributes.attributeModifier();
        }
    }
}