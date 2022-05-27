package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.player.refinements.Refinement;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class ModRefinements {
    public static DeferredRegister<IRefinement> REFINEMENTS = DeferredRegister.create(ModRegistries.REFINEMENT_ID, REFERENCE.MODID);


    public static final RegistryObject<Refinement> ARMOR1 = REFINEMENTS.register("armor1", () -> new Refinement(Attributes.ARMOR, UUID.fromString("fe88a321-acba-4275-af04-e0e2a13bfeb0"), 0.3, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> ARMOR2 = REFINEMENTS.register("armor2", () -> new Refinement(Attributes.ARMOR, UUID.fromString("e610297e-0159-4cb6-ae8c-7d039952f2b0"), 0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> ARMOR3 = REFINEMENTS.register("armor3", () -> new Refinement(Attributes.ARMOR, UUID.fromString("1b784e58-7769-4e37-a56c-78e94e3eb590"), 1, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> N_ARMOR1 = REFINEMENTS.register("n_armor1", () -> new Refinement(Attributes.ARMOR, UUID.fromString("a8f087e6-73c5-4014-9f9e-7aa7d467b361"), -1, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_ARMOR2 = REFINEMENTS.register("n_armor2", () -> new Refinement(Attributes.ARMOR, UUID.fromString("af487a05-7821-4f24-992b-f3d0db440758"), -2, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_ARMOR3 = REFINEMENTS.register("n_armor3", () -> new Refinement(Attributes.ARMOR, UUID.fromString("b6286930-a32f-4a85-a73e-3ee4624de9c5"), -3, (uuid, value) -> new AttributeModifier(uuid, "refinement_armor_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());

    public static final RegistryObject<Refinement> SPEED1 = REFINEMENTS.register("speed1", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("7181faa0-7267-4497-baab-98e57ec5d8db"), 0.025, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> SPEED2 = REFINEMENTS.register("speed2", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("e924ca0b-d55f-421d-b153-3f7907a855b4"), 0.05, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> SPEED3 = REFINEMENTS.register("speed3", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("e81e7082-4673-4a65-8178-8a70279dfb5e"), 0.075, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> N_SPEED1 = REFINEMENTS.register("n_speed1", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("aa945420-8f38-4b43-a5c0-9cc2ee2aaad6"), -0.025, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());
    public static final RegistryObject<Refinement> N_SPEED2 = REFINEMENTS.register("n_speed2", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("f0ab98ed-5839-4ef4-9868-9516cd9b58f3"), -0.05, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());
    public static final RegistryObject<Refinement> N_SPEED3 = REFINEMENTS.register("n_speed3", () -> new Refinement(Attributes.MOVEMENT_SPEED, UUID.fromString("2c20aa81-f7fe-40af-9220-0e435e7cef17"), -0.075, (uuid, value) -> new AttributeModifier(uuid, "refinement_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());

    public static final RegistryObject<Refinement> HEALTH1 = REFINEMENTS.register("health1", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("a1dd8cca-9283-461a-a33c-59dcee1967eb"), 0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> HEALTH2 = REFINEMENTS.register("health2", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("61dc9903-8f5c-4ca1-8ff4-fc546427f57e"), 1, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> HEALTH3 = REFINEMENTS.register("health3", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("fdc8e172-404a-4910-bd1c-526bb17bd393"), 1.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_health", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> N_HEALTH1 = REFINEMENTS.register("n_health1", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("e8275eaf-87ed-4898-b7ea-622006318d58"), -1, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_HEALTH2 = REFINEMENTS.register("n_health2", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("ac88dfb6-68d7-4ac4-96bc-d1906c023378"), -2, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_HEALTH3 = REFINEMENTS.register("n_health3", () -> new Refinement(Attributes.MAX_HEALTH, UUID.fromString("009ee5fa-e8a3-4d27-be6a-1d87ce43883a"), -3, (uuid, value) -> new AttributeModifier(uuid, "refinement_health_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());

    public static final RegistryObject<Refinement> DAMAGE1 = REFINEMENTS.register("damage1", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("708c05bb-a12c-4868-93c1-9f1cb3cd40c8"), 0.15, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> DAMAGE2 = REFINEMENTS.register("damage2", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("7fb3050d-364f-4ced-9587-9549ff90e9e4"), 0.3, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> DAMAGE3 = REFINEMENTS.register("damage3", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("e314753b-e77c-4cf5-bb9f-885049c060d0"), 0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage", value, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<Refinement> N_DAMAGE1 = REFINEMENTS.register("n_damage1", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f44829f6-0321-4410-b750-745689172515"), -0.15, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_DAMAGE2 = REFINEMENTS.register("n_damage2", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f6fabcf6-2daa-43dd-8d93-e6580f88270f"), -0.3, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());
    public static final RegistryObject<Refinement> N_DAMAGE3 = REFINEMENTS.register("n_damage3", () -> new Refinement(Attributes.ATTACK_DAMAGE, UUID.fromString("f936f64b-65f4-4ba0-b35b-2cb5b078cbf0"), -0.5, (uuid, value) -> new AttributeModifier(uuid, "refinement_damage_debuff", value, AttributeModifier.Operation.ADDITION)).setDetrimental());

    public static final RegistryObject<Refinement> ATTACK_SPEED1 = REFINEMENTS.register("attack_speed1", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("b10e0279-f15d-40bc-9551-7faf0bb24265"), 0.03, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> ATTACK_SPEED2 = REFINEMENTS.register("attack_speed2", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("f87cf0e5-8165-4baa-86f9-95ceb99a8e58"), 0.08, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> ATTACK_SPEED3 = REFINEMENTS.register("attack_speed3", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("ae55073d-cf46-4185-ab09-4828c6ae3c1f"), 0.11, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed", value, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<Refinement> N_ATTACK_SPEED1 = REFINEMENTS.register("n_attack_speed1", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("04835850-7441-4ddb-871c-964023f6027b"), -0.05, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());
    public static final RegistryObject<Refinement> N_ATTACK_SPEED2 = REFINEMENTS.register("n_attack_speed2", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("813f4b7f-5aee-407e-a506-872989e7155b"), -0.1, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());
    public static final RegistryObject<Refinement> N_ATTACK_SPEED3 = REFINEMENTS.register("n_attack_speed3", () -> new Refinement(Attributes.ATTACK_SPEED, UUID.fromString("0f9e6e75-8362-4863-978e-9adc337839d6"), -0.15, (uuid, value) -> new AttributeModifier(uuid, "refinement_attack_speed_debuff", value, AttributeModifier.Operation.MULTIPLY_BASE)).setDetrimental());

    // simple refinements
    public static final RegistryObject<Refinement> HALF_INVULNERABLE = REFINEMENTS.register("half_invulnerable", Refinement::new);
    public static final RegistryObject<Refinement> TELEPORT_DISTANCE = REFINEMENTS.register("teleport_distance", Refinement::new);
    public static final RegistryObject<Refinement> SWORD_FINISHER = REFINEMENTS.register("sword_finisher", Refinement::new);
    public static final RegistryObject<Refinement> SUMMON_BATS = REFINEMENTS.register("summon_bats", Refinement::new);
    public static final RegistryObject<Refinement> RAGE_FURY = REFINEMENTS.register("rage_fury", Refinement::new);
    public static final RegistryObject<Refinement> REGENERATION = REFINEMENTS.register("regeneration", Refinement::new);
    public static final RegistryObject<Refinement> SUN_SCREEN = REFINEMENTS.register("sun_screen", Refinement::new);
    public static final RegistryObject<Refinement> DARK_BLOOD_PROJECTILE_PENETRATION = REFINEMENTS.register("dark_blood_projectile_penetration", Refinement::new);
    public static final RegistryObject<Refinement> DARK_BLOOD_PROJECTILE_MULTI_SHOT = REFINEMENTS.register("dark_blood_projectile_multi_shot", Refinement::new);
    public static final RegistryObject<Refinement> DARK_BLOOD_PROJECTILE_AOE = REFINEMENTS.register("dark_blood_projectile_aoe", Refinement::new);
    public static final RegistryObject<Refinement> DARK_BLOOD_PROJECTILE_DAMAGE = REFINEMENTS.register("dark_blood_projectile_damage", Refinement::new);
    public static final RegistryObject<Refinement> DARK_BLOOD_PROJECTILE_SPEED = REFINEMENTS.register("dark_blood_projectile_speed", Refinement::new);
    public static final RegistryObject<Refinement> VISTA = REFINEMENTS.register("vista", Refinement::new);
    public static final RegistryObject<Refinement> FREEZE_DURATION = REFINEMENTS.register("freeze_duration", Refinement::new);
    public static final RegistryObject<Refinement> BLOOD_CHARGE_SPEED = REFINEMENTS.register("blood_charge_speed", Refinement::new);
    public static final RegistryObject<Refinement> SWORD_TRAINED_AMOUNT = REFINEMENTS.register("sword_trained_amount", Refinement::new);


    public static void registerRefinements(IEventBus bus) {
        REFINEMENTS.register(bus);
    }
}
