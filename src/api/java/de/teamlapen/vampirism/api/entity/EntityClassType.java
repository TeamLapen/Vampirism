package de.teamlapen.vampirism.api.entity;

import com.google.common.collect.Maps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static EntityClassType getRandomClass(@NotNull RandomSource rand) {
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

    private final @NotNull AttributeModifier healthModifier;
    private final @NotNull AttributeModifier damageModifier;
    private final @NotNull AttributeModifier speedModifier;

    EntityClassType(double healthModifier, double damageModifier, double speedModifier) {
        this.healthModifier = new AttributeModifier("entity_class_health", healthModifier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.damageModifier = new AttributeModifier("entity_class_damage", damageModifier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        this.speedModifier = new AttributeModifier("entity_class_speed", speedModifier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public @NotNull AttributeModifier getDamageModifier() {
        return damageModifier;
    }

    public @NotNull AttributeModifier getHealthModifier() {
        return healthModifier;
    }

    public @NotNull AttributeModifier getSpeedModifier() {
        return speedModifier;
    }
}