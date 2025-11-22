package de.teamlapen.factions.api.skills;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public class SkillPointProviders {

    private static final BiMap<ResourceLocation, ISkillPointProvider> MODIFIERS = HashBiMap.create();
    public static final Map<ResourceLocation, ISkillPointProvider> MODIFIERS_VIEW = Collections.unmodifiableMap(MODIFIERS);

    public static ResourceLocation getId(ISkillPointProvider modifier) {
        var result = MODIFIERS.inverse().get(modifier);
        if (result == null) {
            throw new IllegalArgumentException("Modifier not registered " + modifier);
        }
        return result;
    }
}
