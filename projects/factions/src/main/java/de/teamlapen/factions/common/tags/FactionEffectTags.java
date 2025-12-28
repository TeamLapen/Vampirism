package de.teamlapen.factions.common.tags;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class FactionEffectTags {
    public static final TagKey<MobEffect> DISABLES_ACTIONS = tag("disables_actions");

    private static TagKey<MobEffect> tag(String name) {
        return TagKey.create(Registries.MOB_EFFECT, FResourceLocation.mod(name));
    }
}
