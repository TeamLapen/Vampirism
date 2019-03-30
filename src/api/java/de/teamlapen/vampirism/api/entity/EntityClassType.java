package de.teamlapen.vampirism.api.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public enum EntityClassType {
    Tank(0.3, 0, 0),
    Fighter(0, 0.1, 0),
    Support(0, 0, 0),
    Caster(0, 0.1, 0),
    Assassin(0, 0, 0.08);

    public static final Map<EntityClassType, Integer> ENTITYCLASSTYPES = Maps.<EntityClassType, Integer>newHashMap();
    public static final Map<Integer, EntityClassType> ID = Maps.<Integer, EntityClassType>newHashMap();

    private AttributeModifier healthModifier;
    private AttributeModifier damageModifier;
    private AttributeModifier speedModifier;

    EntityClassType(double healthModifier, double damageModifier, double speedModifier) {
        this.healthModifier = new AttributeModifier("entity_class_health", healthModifier, 1);
        this.damageModifier = new AttributeModifier("entity_class_damage", damageModifier, 1);
        this.speedModifier = new AttributeModifier("entity_class_speed", speedModifier, 1);
    }

    public static EntityClassType getRandomClass(Random rand) {
        return values()[rand.nextInt(values().length - 1)];
    }

    public AttributeModifier getHealthModifier() {
        return healthModifier;
    }

    public AttributeModifier getDamageModifier() {
        return damageModifier;
    }

    public AttributeModifier getSpeedModifier() {
        return speedModifier;
    }

    public static int getID(EntityClassType entityclasstypes) {
        return ENTITYCLASSTYPES.get(entityclasstypes);
    }

    /**
     * @returns {@link EntityClassType} for the given id
     *          if id < 0 or >= {@link ID#size()} return null
     */
    @Nullable
    public static EntityClassType getEntityClassType(int id) {
        return (id >= ID.size() || id < 0) ? null : ID.get(id);
    }

    static {
        for (EntityClassType entityclasstypes : values()) {
            ENTITYCLASSTYPES.put(entityclasstypes, ENTITYCLASSTYPES.size());
            ID.put(ID.size(), entityclasstypes);
        }
    }
}