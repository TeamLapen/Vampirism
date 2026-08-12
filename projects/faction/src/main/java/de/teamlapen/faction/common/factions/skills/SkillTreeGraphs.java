package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.FactionRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class SkillTreeGraphs {

    private static final Map<RegistryAccess, SkillTreeGraph> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private SkillTreeGraphs() {
    }

    public static SkillTreeGraph get(Level level) {
        return get(level.registryAccess());
    }

    public static SkillTreeGraph get(RegistryAccess access) {
        return CACHE.computeIfAbsent(access, x -> SkillTreeGraph.build(x.lookupOrThrow(FactionRegistries.Keys.SKILL_SEGMENT)));
    }

    public static void invalidate() {
        CACHE.clear();
    }
}
