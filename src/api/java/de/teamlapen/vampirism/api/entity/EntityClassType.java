package de.teamlapen.vampirism.api.entity;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import java.util.Random;

public enum EntityClassType {
    Tank(0.3, 0, 0),
    Fighter(0, 0.1, 0),
    Support(0, 0, 0),
    Caster(0, 0.1, 0),
    Assassin(0, 0, 0.08);

    AttributeModifier healthModifier;
    AttributeModifier damageModifier;
    AttributeModifier speedModifier;

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
}