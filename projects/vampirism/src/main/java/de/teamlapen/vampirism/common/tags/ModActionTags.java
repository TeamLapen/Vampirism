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
    public static final TagKey<IAction<?>> DISABLE_BY_CRUCIFIX = tag("disabled_crucifix");
    public static final TagKey<IAction<?>> DISABLE_BY_NORMAL_CRUCIFIX = tag("disabled_crucifix/normal");
    public static final TagKey<IAction<?>> DISABLE_BY_ENHANCED_CRUCIFIX = tag("disabled_crucifix/enhanced");
    public static final TagKey<IAction<?>> DISABLE_BY_ULTIMATE_CRUCIFIX = tag("disabled_crucifix/ultimate");
    public static final TagKey<IAction<?>> VAMPIRE_FORM_ACTIONS = tag("vampire_form_actions");

    /**
     * @param amplifier the amplifier of the crucifix suppression effect, which corresponds to the crucifix tier
     * @return the tag of actions disabled by a crucifix of the given tier. Higher tiers include the actions of lower tiers
     */
    public static TagKey<IAction<?>> disabledByCrucifix(int amplifier) {
        return switch (amplifier) {
            case 0 -> DISABLE_BY_NORMAL_CRUCIFIX;
            case 1 -> DISABLE_BY_ENHANCED_CRUCIFIX;
            default -> DISABLE_BY_ULTIMATE_CRUCIFIX;
        };
    }

    private static TagKey<IAction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.ACTION, VIdentifier.mod(name));
    }
}

