package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class ModEntityTags {
    public static final TagKey<EntityType<?>> HUNTER = tag("hunter");
    public static final TagKey<EntityType<?>> VAMPIRE = tag("vampire");
    public static final TagKey<EntityType<?>> ADVANCED_HUNTER = tag("advanced_hunter");
    public static final TagKey<EntityType<?>> ADVANCED_VAMPIRE = tag("advanced_vampire");
    public static final TagKey<EntityType<?>> CONVERTED_CREATURES = tag("converted_creatures");

    public static final TagKey<EntityType<?>> ZOMBIES = tag("zombies");
    public static final TagKey<EntityType<?>> IGNORE_VAMPIRE_SWORD_FINISHER = tag("ignore_vampire_sword_finisher");

    private static @NotNull TagKey<EntityType<?>> tag(@NotNull ResourceLocation resourceLocation) {
        return TagKey.create(Registries.ENTITY_TYPE, resourceLocation);
    }

    private static @NotNull TagKey<EntityType<?>> tag(@NotNull String name) {
        return tag(VResourceLocation.mod(name));
    }
}
