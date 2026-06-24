package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class QuarrelHandler {

    private static Set<Item> quarrels;
    private static Set<Item> clips;

    /**
     * collects all registered items that inherit {@link IVampirismQuarrel}
     */
    @ApiStatus.Internal
    public static void collectQuarrels() {
        quarrels = BuiltInRegistries.ITEM.stream().filter(IVampirismQuarrel.class::isInstance).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return all registered items that inherit {@link IVampirismQuarrel}
     */
    public static Set<Item> getQuarrels() {
        return quarrels;
    }

    /**
     * @return all registered clip items (carrying a {@link ModDataComponents#CONTAINED_PROJECTILES} component)
     */
    public static Set<Item> getClips() {
        if (clips == null) {
            clips = BuiltInRegistries.ITEM.stream().filter(item -> item.components().has(ModDataComponents.CONTAINED_PROJECTILES.get())).collect(Collectors.toUnmodifiableSet());
        }
        return clips;
    }
}
