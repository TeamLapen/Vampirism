package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class ModEffectTags {
    public static final TagKey<MobEffect> HUNTER_POTION_RESISTANCE = tag("hunter_potion_resistance");
    public static final TagKey<MobEffect> DISABLES_ACTIONS_HUNTER = tag("disables_actions/hunter");
    public static final TagKey<MobEffect> DISABLES_ACTIONS_VAMPIRE = tag("disables_actions/vampire");
    public static final TagKey<MobEffect> SELF_HARM_SERUMS = tag("self_harm_serums");

    private static TagKey<MobEffect> tag(String name) {
        return TagKey.create(Registries.MOB_EFFECT, VIdentifier.mc(name));
    }
}
