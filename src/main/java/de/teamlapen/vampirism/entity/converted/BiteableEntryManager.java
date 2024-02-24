package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.datamaps.EntityBloodEntry;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BiteableEntryManager {
    private final static Logger LOGGER = LogManager.getLogger();

    @NotNull
    private final Map<EntityType<?>, IEntityBlood> calculatedEntries = new HashMap<>();

    /**
     * Calculate the blood value for the given creature
     * If the result is 0 blood this returns null and the entity is blacklisted
     *
     * @return The created entry or null
     */
    @NotNull
    public IEntityBlood calculate(@NotNull PathfinderMob creature) {
        if (!VampirismConfig.SERVER.autoCalculateEntityBlood.get()) return EntityBloodEntry.EMPTY;
        EntityType<?> type = creature.getType();
        ResourceLocation id = RegUtil.id(type);
        if (isEntityBlacklisted(creature)) {
            return EntityBloodEntry.EMPTY;
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
            return EntityBloodEntry.EMPTY;
        } else {
            return new EntityBloodEntry(blood);
        }
    }

    /**
     * returns an existing entry
     *
     * @param creature for which a {@link de.teamlapen.vampirism.api.datamaps.IEntityBlood} is requested
     * @return {@code null} if resources aren't loaded or the creatures type is blacklisted.
     */
    @Nullable
    public IEntityBlood get(@NotNull PathfinderMob creature) {
        if (calculatedEntries.containsKey(creature.getType())) {
            return calculatedEntries.get(creature.getType());
        }
        return BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(creature.getType()).getData(ModRegistries.ENTITY_BLOOD_MAP);
    }

    /**
     * returns an existing entry or creates a new one
     *
     * @param creature for which a {@link de.teamlapen.vampirism.api.datamaps.IEntityBlood} is requested
     * @return {@code null} if resources aren't loaded or the creatures type is blacklisted.
     */
    @NotNull
    public IEntityBlood getOrCalculate(@NotNull PathfinderMob creature) {
        IEntityBlood entry = get(creature);
        if (entry == null) {
            entry = calculate(creature);
            calculatedEntries.put(creature.getType(), entry);
        }
        return entry;
    }

    /**
     * checks if the entity type is blacklisted through the server config
     *
     * @param id registryname of the entity type
     * @return weather the entity type is blacklisted by the server config or not
     */
    private boolean isConfigBlackListed(@NotNull ResourceLocation id) {
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
        if (type.getCategory() == MobCategory.MONSTER || type.getCategory() == MobCategory.WATER_CREATURE) {
            return true;
        }
        if (type.is(ModTags.Entities.VAMPIRE)) return true;
        return isConfigBlackListed(RegUtil.id(type));
    }
}
