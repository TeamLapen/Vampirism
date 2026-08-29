package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Caches the built {@link SkillTreeGraph} per registry set. The graph is derived from the loaded skill segments, so a
 * new one is built whenever the datapacks change, and the client and the server each keep their own.
 * <p>
 * Both sides build one instead of the server sending its graph over. The segments are synced already and the graph
 * holds nothing that cannot be derived from them, while its entries reference each other and the registry, so it would
 * have to be flattened to keys and linked up again on arrival anyway. The server needs a graph of its own for unlock
 * validation regardless, and the client for the skill screen and the checks it runs before asking for an unlock.
 * <p>
 * This system should be better than sending the graph from the server to clients via a packet as clients already have
 * all the registry information they need and there's nothing really dependent on the server. The only weakness (in
 * theory) is that a graph must not reference its own registry access, otherwise it would stay forever in memory.
 */
public class SkillTreeGraphs {

    /**
     * Weak keys, so a graph is dropped once its registries are gone. Synchronized as both sides reach for it. A cached
     * graph must never reference the access it is keyed by, or the entries stop being collectable.
     * <p>
     * NEVER reference the registry access inside the graph it is keyed by. Weak keys are dropped when they are
     * unreachable, but doing this would make the entry immortal. It would just stay and eat the space.
     */
    private static final Map<RegistryAccess, Cached> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private SkillTreeGraphs() {
    }

    public static SkillTreeGraph get(Level level) {
        return get(level.registryAccess());
    }

    /**
     * Rebuilds if the registry was swapped out underneath the access. The build runs outside the lock, so two callers
     * racing for the same access may build twice, which should be harmless as entries compare by key.
     */
    public static SkillTreeGraph get(RegistryAccess access) {
        HolderLookup.RegistryLookup<ISkillSegment> registry = access.lookupOrThrow(FactionRegistries.Keys.SKILL_SEGMENT);
        Cached cached = CACHE.get(access);
        if (cached != null && cached.registry() == registry) {
            return cached.graph();
        }

        SkillTreeGraph graph = SkillTreeGraph.build(registry);
        CACHE.put(access, new Cached(registry, graph));
        return graph;
    }

    /**
     * Drops the graph of the given access, or every graph if the access is gone.
     */
    public static void invalidate(@Nullable RegistryAccess access) {
        if (access == null) {
            CACHE.clear();
        } else {
            CACHE.remove(access);
        }
    }

    private record Cached(HolderLookup.RegistryLookup<ISkillSegment> registry, SkillTreeGraph graph) {}
}
