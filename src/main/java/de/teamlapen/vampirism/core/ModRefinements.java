package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.player.refinements.Refinement;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.UUID;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModRefinements {

    public static final Refinement armor1 = getNull();
    public static final Refinement armor2 = getNull();
    public static final Refinement armor3 = getNull();
    public static final Refinement n_armor1 = getNull();
    public static final Refinement n_armor2 = getNull();
    public static final Refinement n_armor3 = getNull();
    public static final Refinement speed1 = getNull();
    public static final Refinement speed2 = getNull();
    public static final Refinement speed3 = getNull();
    public static final Refinement n_speed1 = getNull();
    public static final Refinement n_speed2 = getNull();
    public static final Refinement n_speed3 = getNull();
    public static final Refinement health1 = getNull();
    public static final Refinement health2 = getNull();
    public static final Refinement health3 = getNull();
    public static final Refinement n_health1 = getNull();
    public static final Refinement n_health2 = getNull();
    public static final Refinement n_health3 = getNull();
    public static final Refinement damage1 = getNull();
    public static final Refinement damage2 = getNull();
    public static final Refinement damage3 = getNull();
    public static final Refinement n_damage1 = getNull();
    public static final Refinement n_damage2 = getNull();
    public static final Refinement n_damage3 = getNull();
    public static final Refinement attack_speed1 = getNull();
    public static final Refinement attack_speed2 = getNull();
    public static final Refinement attack_speed3 = getNull();
    public static final Refinement n_attack_speed1 = getNull();
    public static final Refinement n_attack_speed2 = getNull();
    public static final Refinement n_attack_speed3 = getNull();
    public static final Refinement half_invulnerable = getNull();
    public static final Refinement teleport_distance = getNull();
    public static final Refinement sword_finisher = getNull();
    public static final Refinement summon_bats = getNull();
    public static final Refinement rage_fury = getNull();
    public static final Refinement regeneration = getNull();
    public static final Refinement sun_screen = getNull();
    public static final Refinement dark_blood_projectile_penetration = getNull();
    public static final Refinement dark_blood_projectile_multi_shot = getNull();
    public static final Refinement dark_blood_projectile_aoe = getNull();
    public static final Refinement dark_blood_projectile_damage = getNull();
    public static final Refinement vista = getNull();
    public static final Refinement freeze_duration = getNull();
    public static final Refinement blood_charge_speed = getNull();
    public static final Refinement sword_trained_amount = getNull();



    public static void registerRefinements(@Nonnull IForgeRegistry<IRefinement> registry) {
        {
            {
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("fe88a321-acba-4275-af04-e0e2a13bfeb0"), 1, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "armor1"));
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("e610297e-0159-4cb6-ae8c-7d039952f2b0"), 2, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "armor2"));
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("1b784e58-7769-4e37-a56c-78e94e3eb590"), 3, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "armor3"));
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("a8f087e6-73c5-4014-9f9e-7aa7d467b361"), -1, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_armor1"));
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("af487a05-7821-4f24-992b-f3d0db440758"), -2, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_armor2"));
                registry.register(new Refinement(Attributes.ARMOR, UUID.fromString("b6286930-a32f-4a85-a73e-3ee4624de9c5"), -3, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_armor3"));
            }
            {
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("7181faa0-7267-4497-baab-98e57ec5d8db"), 0.05, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "speed1"));
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("e924ca0b-d55f-421d-b153-3f7907a855b4"), 0.1, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "speed2"));
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("e81e7082-4673-4a65-8178-8a70279dfb5e"), 0.15, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "speed3"));
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("aa945420-8f38-4b43-a5c0-9cc2ee2aaad6"), -0.05, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_speed1"));
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("f0ab98ed-5839-4ef4-9868-9516cd9b58f3"), -0.1, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_speed2"));
                registry.register(new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("2c20aa81-f7fe-40af-9220-0e435e7cef17"), -0.15, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_speed3"));
            }
            {
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("a1dd8cca-9283-461a-a33c-59dcee1967eb"), 1, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "health1"));
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("61dc9903-8f5c-4ca1-8ff4-fc546427f57e"), 2, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "health2"));
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("fdc8e172-404a-4910-bd1c-526bb17bd393"), 3, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "health3"));
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("e8275eaf-87ed-4898-b7ea-622006318d58"), -1, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_health1"));
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("ac88dfb6-68d7-4ac4-96bc-d1906c023378"), -2, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_health2"));
                registry.register(new Refinement(Attributes.MAX_HEALTH, UUID.fromString("009ee5fa-e8a3-4d27-be6a-1d87ce43883a"), -3, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_health3"));
            }
            {
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("708c05bb-a12c-4868-93c1-9f1cb3cd40c8"), 0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "damage1"));
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("7fb3050d-364f-4ced-9587-9549ff90e9e4"), 1, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "damage2"));
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("e314753b-e77c-4cf5-bb9f-885049c060d0"), 1.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "damage3"));
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f44829f6-0321-4410-b750-745689172515"), -0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_damage1"));
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f6fabcf6-2daa-43dd-8d93-e6580f88270f"), -1, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_damage2"));
                registry.register(new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f936f64b-65f4-4ba0-b35b-2cb5b078cbf0"), -1.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setRegistryName(REFERENCE.MODID, "n_damage3"));
            }
            {
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("b10e0279-f15d-40bc-9551-7faf0bb24265"), 0.1, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "attack_speed1"));
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("f87cf0e5-8165-4baa-86f9-95ceb99a8e58"), 0.2, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "attack_speed2"));
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("ae55073d-cf46-4185-ab09-4828c6ae3c1f"), 0.3, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "attack_speed3"));
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("04835850-7441-4ddb-871c-964023f6027b"), -0.1, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_attack_speed1"));
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("813f4b7f-5aee-407e-a506-872989e7155b"), -0.2, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_attack_speed2"));
                registry.register(new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("0f9e6e75-8362-4863-978e-9adc337839d6"), -0.3, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setRegistryName(REFERENCE.MODID, "n_attack_speed3"));
            }
        }
        { // simple refinements
            registry.register(newRefinement("half_invulnerable"));
            registry.register(newRefinement("teleport_distance"));
            registry.register(newRefinement("sword_finisher"));
            registry.register(newRefinement("summon_bats"));
            registry.register(newRefinement("rage_fury"));
            registry.register(newRefinement("regeneration"));
            registry.register(newRefinement("sun_screen"));
            registry.register(newRefinement("dark_blood_projectile_penetration"));
            registry.register(newRefinement("dark_blood_projectile_multi_shot"));
            registry.register(newRefinement("dark_blood_projectile_aoe"));
            registry.register(newRefinement("vista"));
            registry.register(newRefinement("freeze_duration"));
            registry.register(newRefinement("blood_charge_speed"));
            registry.register(newRefinement("sword_trained_amount"));
        }
    }


    private static IRefinement newRefinement(String name) {
        return new Refinement().setRegistryName(REFERENCE.MODID, name);
    }
}
