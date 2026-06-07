package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class QuarrelHandler {

    private static Set<Item> quarrels;

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
}
