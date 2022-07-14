package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.BloodValues;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Manages biteable entries.
 * Get values from various sources
 * Static values present in datapacks from {@link BloodValues#ENTITIES}
 * Dynamically calculated values from itself
 * Dynamically saved values on world load from {@link BloodValues#ENTITIES}}
 * <p>
 * <p>
 * Dynamic values are calculated during gameplay and saved on stopping (server).
 * Values are currently not synced between server and client, however, ExtendedCreatures do so.
 */
public class BiteableEntryManager {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nonnull
    private final Map<ResourceLocation, BiteableEntry> biteableEntries = Maps.newHashMap();
    @Nonnull
    private final Map<ResourceLocation, BiteableEntry> calculated = Maps.newHashMap();
    @Nonnull
    private final Set<ResourceLocation> blacklist = Sets.newHashSet();

    private boolean initialized = false;

    /**
     * see {@link #addCalculated(ResourceLocation, int)}
     */
    public void addCalculated(Map<ResourceLocation, Integer> map) {
        for (Map.Entry<ResourceLocation, Integer> e : map.entrySet()) {
            addCalculated(e.getKey(), e.getValue());
        }
    }

    /**
     * Calculate the blood value for the given creature
     * If the result is 0 blood this returns null and the entity is blacklisted
     *
     * @return The created entry or null
     */
    @Nullable
    public BiteableEntry calculate(@Nonnull PathfinderMob creature) {
        if (!VampirismConfig.SERVER.autoCalculateEntityBlood.get()) return null;
        EntityType<?> type = creature.getType();
        @Nullable
        ResourceLocation id = RegUtil.id(type);
        if (id == null) return null;
        if (blacklist.contains(id)) return null;
        if (isEntityBlacklisted(creature)) {
            blacklist.add(id);
            return null;
        }
        AABB bb = creature.getBoundingBox();
        double v = bb.maxX - bb.minX;
        v *= bb.maxY - bb.minY;
        v *= bb.maxZ - bb.minZ;
        if (creature.isBaby()) {
            v *= 8; //Rough approximation. Should work for most vanilla animals. Avoids having to change the entities scale
        }
        int blood = 0;

        if (v >= 0.3) {
            blood = (int) (v * 7d);
            blood = Math.min(15, blood);//Make sure there are no too crazy values
        }
        if (creature.getMaxHealth() > 50) {
            blood = 0;//Make sure very strong creatures cannot be easily killed by sucking their blood
        }
        LOGGER.debug("Calculated size {} and blood value {} for entity {}", Math.round(v * 100) / 100F, blood, id);
        if (blood == 0) {
            blacklist.add(id);
            return null;
        } else {
            return addCalculated(id, blood);
        }
    }

    /**
     * @param creature for which a {@link BiteableEntry} is requested
     * @return {@code null} if resources aren't loaded or the creatures type is blacklisted.
     */
    @Nullable
    public BiteableEntry get(PathfinderMob creature) {
        if (!initialized) return null;
        EntityType<?> type = creature.getType();
        ResourceLocation id = EntityType.getKey(type);
        if (isConfigBlackListed(id)) return null;
        if (biteableEntries.containsKey(id)) return biteableEntries.get(id);
        return calculated.getOrDefault(id, null);
    }

    /**
     * Get all calculated values
     *
     * @return map of entities, which are not present in data folder, to calculated blood values
     */
    public Map<ResourceLocation, Integer> getValuesToSave() {
        Map<ResourceLocation, Integer> map = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, BiteableEntry> entry : calculated.entrySet()) {
            if (!biteableEntries.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue().blood);
            }
        }
        return map;
    }

    public boolean init() {
        return initialized;
    }

    /**
     * Adds a calculated value.
     *
     * @return The created entry
     */
    private BiteableEntry addCalculated(ResourceLocation id, int blood) {
        BiteableEntry existing = calculated.containsKey(id) ? calculated.get(id).modifyBloodValue(blood) : new BiteableEntry(blood);
        calculated.put(id, existing);
        return existing;
    }

    /**
     * checks if the entity type is blacklisted through the server config
     *
     * @param id registryname of the entity type
     * @return weather the entity type is blacklisted by the server config or not
     */
    private boolean isConfigBlackListed(ResourceLocation id) {
        List<? extends String> list = VampirismConfig.SERVER.blacklistedBloodEntity.get();
        return list.contains(id.toString());
    }

    /**
     * checks if the creature entity is blacklisted
     *
     * @param creature the entity to check
     * @return weather the entity is blacklisted or not
     */
    private boolean isEntityBlacklisted(PathfinderMob creature) {
        if (!(creature instanceof Animal)) return true;
        if (creature instanceof IVampire) return true;
        EntityType<?> type = creature.getType();
        if (type.getCategory() == MobCategory.MONSTER || type.getCategory() == MobCategory.WATER_CREATURE)
            return true;
        if (type.is(ModTags.Entities.VAMPIRE)) return true;
        return isConfigBlackListed(RegUtil.id(type));
    }

    void setNewBiteables(Map<ResourceLocation, BiteableEntry> biteableEntries, Set<ResourceLocation> blacklist) {
        this.biteableEntries.clear();
        this.blacklist.clear();
        this.biteableEntries.putAll(biteableEntries);
        this.blacklist.addAll(blacklist);
        initialized = true;
    }
}
