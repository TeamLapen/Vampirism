package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModActionTags {

    public static final TagKey<IAction<?>> DISABLE_BY_HOLY_WATER = tag("disabled_holy_water");
    public static final TagKey<IAction<?>> DISABLE_BY_NORMAL_HOLY_WATER = tag("disabled_holy_water/weak");
    public static final TagKey<IAction<?>> DISABLE_BY_ENHANCED_HOLY_WATER = tag("disabled_holy_water/enhanced");
    public static final TagKey<IAction<?>> DISABLE_BY_ULTIMATE_HOLY_WATER = tag("disabled_holy_water/ultimate");

    private static @NotNull TagKey<IAction<?>> tag(@NotNull String name) {
        return TagKey.create(VampirismRegistries.Keys.ACTION, VResourceLocation.mod(name));
    }
}

