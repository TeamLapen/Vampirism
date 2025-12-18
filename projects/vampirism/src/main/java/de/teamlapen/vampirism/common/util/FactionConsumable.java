package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.common.world.items.consume.BloodFoodProperties;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.ConsumableListener;

import java.util.*;
import java.util.stream.Stream;

public record FactionConsumable() {

    public static final FactionConsumable FOOD = new FactionConsumable();

    private static final Map<FactionConsumable, List<Class<? extends ConsumableListener>>> CONSUMABLES = new HashMap<>();
    private static final Map<Class<? extends ConsumableListener>, FactionConsumable> LISTENERS = new HashMap<>();

    public static void register(FactionConsumable consumable, Class<? extends ConsumableListener> listener) {
        CONSUMABLES.computeIfAbsent(consumable, k -> new ArrayList<>()).add(listener);
        LISTENERS.put(listener, consumable);
    }

    public static Stream<Class<? extends ConsumableListener>> getFor(FactionConsumable consumable) {
        return CONSUMABLES.getOrDefault(consumable, Collections.emptyList()).stream();
    }

    public static FactionConsumable getFor(Class<? extends ConsumableListener> listener) {
        return LISTENERS.get(listener);
    }

    static {
        register(FOOD, FoodProperties.class);
        register(FOOD, BloodFoodProperties.class);
    }
}
