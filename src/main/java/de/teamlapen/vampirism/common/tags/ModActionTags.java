package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class ModActionTags {
    public static final TagKey<IAction<?>> DISABLE_BY_HOLY_WATER = tag("disabled_holy_water");
    public static final TagKey<IAction<?>> DISABLE_BY_NORMAL_HOLY_WATER = tag("disabled_holy_water/weak");
    public static final TagKey<IAction<?>> DISABLE_BY_ENHANCED_HOLY_WATER = tag("disabled_holy_water/enhanced");
    public static final TagKey<IAction<?>> DISABLE_BY_ULTIMATE_HOLY_WATER = tag("disabled_holy_water/ultimate");

    private static TagKey<IAction<?>> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.ACTION, VResourceLocation.mod(name));
    }
}

