package de.teamlapen.vampirism.entity.villager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * just {@link VillagerHostilesSensor} with a hostile map without zombies
 */
public class VampireVillagerHostilesSensor extends VillagerHostilesSensor {
    public static final Map<EntityType<?>, Float> HOSTILE_ENTITY_TYPES;

    static {
        //Adding entries will probably not work outside Dev as func_220988_c is not called for some reason
        HOSTILE_ENTITY_TYPES = new HashMap<>(VillagerHostilesSensor.field_220991_b);
        HOSTILE_ENTITY_TYPES.remove(EntityType.ZOMBIE);
        HOSTILE_ENTITY_TYPES.remove(EntityType.ZOMBIE_VILLAGER);
        HOSTILE_ENTITY_TYPES.remove(EntityType.DROWNED);
        HOSTILE_ENTITY_TYPES.remove(EntityType.HUSK);
    }

    @Override
    public boolean func_220987_a(@Nonnull LivingEntity villagerEntity, LivingEntity hostileEntity) { //public to avoid AT issues
        //func_220988_c is not checked first, so entries may not be present
        @Nullable Float f = HOSTILE_ENTITY_TYPES.get(hostileEntity.getType()); //Careful about unboxing nullpointer
        if (f == null) return false;
        return hostileEntity.getDistanceSq(villagerEntity) <= (double) (f * f);
    }


    @Override
    public boolean func_220988_c(LivingEntity hostile) { //For some reason this method is not called (as it does not properly override somehow maybe) outside dev
        return HOSTILE_ENTITY_TYPES.containsKey(hostile.getType());
    }

}
