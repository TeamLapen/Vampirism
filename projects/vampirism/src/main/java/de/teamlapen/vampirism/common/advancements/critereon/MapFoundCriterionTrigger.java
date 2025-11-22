package de.teamlapen.vampirism.common.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.common.util.MapUtil;
import de.teamlapen.vampirism.common.core.ModAdvancements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MapFoundCriterionTrigger extends SimpleCriterionTrigger<MapFoundCriterionTrigger.TriggerInstance> {

    @Override
    public @NotNull Codec<MapFoundCriterionTrigger.TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Inventory inventory) {
        this.trigger(player, predicate -> predicate.matches(inventory, player.level()));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Holder<MapDecorationType>> mapType) implements SimpleInstance {
        public static final Codec<MapFoundCriterionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(MapFoundCriterionTrigger.TriggerInstance::player),
                                MapDecorationType.CODEC.optionalFieldOf("mapType").forGetter(MapFoundCriterionTrigger.TriggerInstance::mapType)
                        )
                        .apply(instance, MapFoundCriterionTrigger.TriggerInstance::new)
        );

        public static Criterion<MapFoundCriterionTrigger.TriggerInstance> foundMap(Holder<MapDecorationType> mapType) {
            return ModAdvancements.TRIGGER_MAP_FOUND.get()
                    .createCriterion(
                            new MapFoundCriterionTrigger.TriggerInstance(Optional.empty(), Optional.of(mapType))
                    );
        }

        public boolean matches(Inventory inventory, Level level) {
            for (ItemStack itemStack : inventory.getNonEquipmentItems()) {
                if (mapType.isPresent() && MapUtil.hasDecoration(mapType.get(), itemStack, level)) {
                    return true;
                }
            }

            return false;
        }
    }
}
