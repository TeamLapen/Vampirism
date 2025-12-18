package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class CrossbowArrowHandler {

    private static Set<Item> crossbowArrows;

    /**
     * collects all registered items that inherit {@link IVampirismCrossbowArrow}
     */
    @ApiStatus.Internal
    public static void collectCrossbowArrows() {
        crossbowArrows = BuiltInRegistries.ITEM.stream().filter(IVampirismCrossbowArrow.class::isInstance).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return all registered items that inherit {@link IVampirismCrossbowArrow}
     */
    public static Set<Item> getCrossbowArrows() {
        return crossbowArrows;
    }
}
