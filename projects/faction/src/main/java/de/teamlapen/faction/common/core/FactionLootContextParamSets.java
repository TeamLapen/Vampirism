package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public class FactionLootContextParamSets {

    /**
     * Used by {@link de.teamlapen.faction.common.factions.minions.management.CollectResourcesTask}.
     * The tool is the item in the minion's main hand.
     */
    public static final ContextKeySet MINION_COLLECT = register("minion_collect", builder -> builder.required(LootContextParams.TOOL).optional(FactionLootContextParams.LORD_PLAYER));

    private static ContextKeySet register(String name, Consumer<ContextKeySet.Builder> consumer) {
        ContextKeySet.Builder builder = new ContextKeySet.Builder();
        consumer.accept(builder);
        ContextKeySet result = builder.build();
        Identifier id = FIdentifier.mod(name);
        if (LootContextParamSets.REGISTRY.put(id, result) != null) {
            throw new IllegalStateException("Loot table parameter set " + id + " is already registered");
        }
        return result;
    }

    /**
     * Makes sure the parameter sets are registered before any loot table is loaded
     */
    @ApiStatus.Internal
    static void register() {
    }
}
