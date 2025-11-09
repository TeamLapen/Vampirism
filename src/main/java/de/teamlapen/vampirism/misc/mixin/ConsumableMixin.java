package de.teamlapen.vampirism.misc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.common.tags.ModDataComponentTags;
import de.teamlapen.vampirism.common.util.FactionConsumable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(Consumable.class)
public class ConsumableMixin {

    @WrapOperation(method = "onConsume", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void skipWrongFactions(Stream<ConsumableListener> instance, Consumer<ConsumableListener> consumer, Operation<Void> original, Level level, LivingEntity entity, ItemStack stack) {
        //noinspection unchecked
        var entityFaction = (Holder<IFaction<?>>) VampirismAPI.factionRegistry().getFaction(entity);
        var componentRegistry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        var factionBasedConsumables = new HashMap<FactionConsumable, List<Pair<Holder<DataComponentType<?>>, ConsumableListener>>>();
        var defaultConsumable = new ArrayList<ConsumableListener>();
        //noinspection unchecked
        stack.getComponents().stream().filter(s -> ConsumableListener.class.isAssignableFrom(s.value().getClass())).map(s -> (TypedDataComponent<ConsumableListener>)s).forEach(component -> {
            FactionConsumable aFor = FactionConsumable.getFor(component.value().getClass());
            if (aFor == null) {
                defaultConsumable.add(component.value());
            } else {
                factionBasedConsumables.computeIfAbsent(aFor, x -> new ArrayList<>()).add(Pair.of(componentRegistry.wrapAsHolder(component.type()), component.value()));
            }
        });

        // consume all that are not registered as FactionConsumable
        defaultConsumable.forEach(consumer);

        var tag = Optional.ofNullable(entityFaction).map(Holder::value).flatMap(s -> s.getTag(Registries.DATA_COMPONENT_TYPE)).orElse(null);

        for (var classListEntry : factionBasedConsumables.entrySet()) {

            // consume all that are not tagged with faction food
            classListEntry.getValue().stream().filter(s -> !s.getKey().is(ModDataComponentTags.FACTION_FOOD)).map(Pair::getValue).forEach(consumer);

            // list all that are tagged with faction food
            var faction = classListEntry.getValue().stream().filter(s -> s.getKey().is(ModDataComponentTags.FACTION_FOOD)).toList();

            // list all that are tagged with the entity's faction
            var fac = tag != null ? faction.stream().filter(x -> x.getKey().is(tag)).toList() : List.<Pair<Holder<DataComponentType<?>>, ConsumableListener>>of();

            // in case no faction food is present, list all base foods
            if (fac.isEmpty()) {
                fac = faction.stream().filter(x -> x.getKey().is(ModDataComponentTags.BASE_FOOD)).toList();
            }

            // consume faction foods
            fac.stream().map(Pair::getValue).forEach(consumer);
        }
    }
}
