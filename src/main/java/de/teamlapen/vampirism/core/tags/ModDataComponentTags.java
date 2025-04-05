package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModDataComponentTags {
    public static final TagKey<DataComponentType<?>> HUNTER_FOOD = tag("food/faction/hunter");
    public static final TagKey<DataComponentType<?>> VAMPIRE_FOOD = tag("food/faction/vampire");
    public static final TagKey<DataComponentType<?>> BASE_FOOD = tag("food/faction/all");
    public static final TagKey<DataComponentType<?>> FACTION_FOOD = tag("food/faction");

    private static @NotNull TagKey<DataComponentType<?>> tag(@NotNull String name) {
        return TagKey.create(Registries.DATA_COMPONENT_TYPE, VResourceLocation.mod(name));
    }
}
