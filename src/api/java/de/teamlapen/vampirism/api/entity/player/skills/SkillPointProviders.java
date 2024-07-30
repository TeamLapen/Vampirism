package de.teamlapen.vampirism.api.entity.player.skills;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public class SkillPointProviders {

    private static final BiMap<ResourceLocation, ISkillPointProvider> MODIFIERS = HashBiMap.create();
    public static final Map<ResourceLocation, ISkillPointProvider> MODIFIERS_VIEW = Collections.unmodifiableMap(MODIFIERS);

    static {
        SkillPointProviders.register(VResourceLocation.mod("none"), (factionPlayer, tree) -> 0);
    }

    public static ISkillPointProvider register(ResourceLocation id, ISkillPointProvider modifier) {
        if (MODIFIERS.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate modifier id " + id);
        }
        MODIFIERS.put(id, modifier);
        return modifier;
    }

    public static ResourceLocation getId(ISkillPointProvider modifier) {
        var result = MODIFIERS.inverse().get(modifier);
        if (result == null) {
            throw new IllegalArgumentException("Modifier not registered " + modifier);
        }
        return result;
    }
}
