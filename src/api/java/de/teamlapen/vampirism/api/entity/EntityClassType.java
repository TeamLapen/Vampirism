package de.teamlapen.vampirism.api.entity;

import com.google.common.collect.Maps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.Map;

public enum EntityClassType {
    None(0, 0, 0),
    Tank(0.3, 0, 0),
    Fighter(0, 0.1, 0),
    Support(0, 0, 0),
    Caster(0, 0.1, 0),
    Assassin(0, 0, 0.08);

    public static final Map<EntityClassType, Integer> ENTITY_CLASS_TYPES = Maps.newHashMap();
    public static final Map<Integer, EntityClassType> ID = Maps.newHashMap();

    static {
        for (EntityClassType entityclasstype : values()) {
            ENTITY_CLASS_TYPES.put(entityclasstype, ENTITY_CLASS_TYPES.size());
            ID.put(ID.size(), entityclasstype);
        }
    }

    public static EntityClassType getRandomClass(RandomSource rand) {
        return values()[rand.nextInt(values().length - 1)];
    }

    public static int getID(EntityClassType entityclasstype) {
        return ENTITY_CLASS_TYPES.get(entityclasstype);
    }

    /**
     * @return {@link EntityClassType} for the given id
     * if id < 0 or >= {@link #ID#size}return null
     */
    @Nullable
    public static EntityClassType getEntityClassType(int id) {
        return (id >= ID.size() || id < 0) ? null : ID.get(id);
    }

    private final AttributeModifier healthModifier;
    private final AttributeModifier damageModifier;
    private final AttributeModifier speedModifier;

    EntityClassType(double healthModifier, double damageModifier, double speedModifier) {
        this.healthModifier = new AttributeModifier("entity_class_health", healthModifier, AttributeModifier.Operation.MULTIPLY_BASE);
        this.damageModifier = new AttributeModifier("entity_class_damage", damageModifier, AttributeModifier.Operation.MULTIPLY_BASE);
        this.speedModifier = new AttributeModifier("entity_class_speed", speedModifier, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public AttributeModifier getDamageModifier() {
        return damageModifier;
    }

    public AttributeModifier getHealthModifier() {
        return healthModifier;
    }

    public AttributeModifier getSpeedModifier() {
        return speedModifier;
    }
}