package de.teamlapen.vampirism.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class ModActionTags {
    public static final TagKey<IAction<?>> DISABLE_BY_HOLY_WATER = tag("disabled_holy_water");
    public static final TagKey<IAction<?>> DISABLE_BY_NORMAL_HOLY_WATER = tag("disabled_holy_water/weak");
    public static final TagKey<IAction<?>> DISABLE_BY_ENHANCED_HOLY_WATER = tag("disabled_holy_water/enhanced");
    public static final TagKey<IAction<?>> DISABLE_BY_ULTIMATE_HOLY_WATER = tag("disabled_holy_water/ultimate");
    public static final TagKey<IAction<?>> VAMPIRE_FORM_ACTIONS = tag("vampire_form_actions");

    private static TagKey<IAction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.ACTION, VIdentifier.mod(name));
    }
}

