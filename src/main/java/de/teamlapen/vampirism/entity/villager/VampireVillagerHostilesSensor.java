package de.teamlapen.vampirism.entity.villager;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * just {@link VillagerHostilesSensor} with a hostile map without zombies
 */
public class VampireVillagerHostilesSensor extends VillagerHostilesSensor {
    public static final Map<EntityType<?>, Float> hostiles;

    static {
        hostiles = Maps.newHashMap(VillagerHostilesSensor.enemyPresenceRange);
        hostiles.remove(EntityType.ZOMBIE);
        hostiles.remove(EntityType.ZOMBIE_VILLAGER);
        hostiles.remove(EntityType.DROWNED);
        hostiles.remove(EntityType.HUSK);
    }

    @Override
    public boolean canNoticePresence(@Nonnull LivingEntity villager, LivingEntity hostile) { //public to avoid AT issues
        float f = hostiles.get(hostile.getType());
        return hostile.getDistanceSq(villager) <= (double) (f * f);
    }

    @Override
    public boolean hasPresence(LivingEntity hostile) { //public to avoid AT issues
        return hostiles.containsKey(hostile.getType());
    }

}
