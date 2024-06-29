package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.entity.player.refinements.Refinement;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRefinements {
    public static final DeferredRegister<IRefinement> REFINEMENTS = DeferredRegister.create(VampirismRegistries.Keys.REFINEMENT, REFERENCE.MODID);


    public static final DeferredHolder<IRefinement, Refinement> ARMOR1 = REFINEMENTS.register("armor1", () -> new Refinement(Attributes.ARMOR, 0.3, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> ARMOR2 = REFINEMENTS.register("armor2", () -> new Refinement(Attributes.ARMOR, 0.5, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> ARMOR3 = REFINEMENTS.register("armor3", () -> new Refinement(Attributes.ARMOR, 1, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> N_ARMOR1 = REFINEMENTS.register("n_armor1", () -> new Refinement(Attributes.ARMOR, -1, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_ARMOR2 = REFINEMENTS.register("n_armor2", () -> new Refinement(Attributes.ARMOR, -2, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_ARMOR3 = REFINEMENTS.register("n_armor3", () -> new Refinement(Attributes.ARMOR, -3, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());

    public static final DeferredHolder<IRefinement, Refinement> SPEED1 = REFINEMENTS.register("speed1", () -> new Refinement(Attributes.MOVEMENT_SPEED, 0.025, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> SPEED2 = REFINEMENTS.register("speed2", () -> new Refinement(Attributes.MOVEMENT_SPEED, 0.05, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> SPEED3 = REFINEMENTS.register("speed3", () -> new Refinement(Attributes.MOVEMENT_SPEED, 0.075, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> N_SPEED1 = REFINEMENTS.register("n_speed1", () -> new Refinement(Attributes.MOVEMENT_SPEED, -0.025, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_SPEED2 = REFINEMENTS.register("n_speed2", () -> new Refinement(Attributes.MOVEMENT_SPEED, -0.05, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_SPEED3 = REFINEMENTS.register("n_speed3", () -> new Refinement(Attributes.MOVEMENT_SPEED, -0.075, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());

    public static final DeferredHolder<IRefinement, Refinement> HEALTH1 = REFINEMENTS.register("health1", () -> new Refinement(Attributes.MAX_HEALTH, 0.5, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> HEALTH2 = REFINEMENTS.register("health2", () -> new Refinement(Attributes.MAX_HEALTH, 1, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> HEALTH3 = REFINEMENTS.register("health3", () -> new Refinement(Attributes.MAX_HEALTH, 1.5, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> N_HEALTH1 = REFINEMENTS.register("n_health1", () -> new Refinement(Attributes.MAX_HEALTH, -1, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_HEALTH2 = REFINEMENTS.register("n_health2", () -> new Refinement(Attributes.MAX_HEALTH, -2, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_HEALTH3 = REFINEMENTS.register("n_health3", () -> new Refinement(Attributes.MAX_HEALTH, -3, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());

    public static final DeferredHolder<IRefinement, Refinement> DAMAGE1 = REFINEMENTS.register("damage1", () -> new Refinement(Attributes.ATTACK_DAMAGE, 0.15, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> DAMAGE2 = REFINEMENTS.register("damage2", () -> new Refinement(Attributes.ATTACK_DAMAGE, 0.3, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> DAMAGE3 = REFINEMENTS.register("damage3", () -> new Refinement(Attributes.ATTACK_DAMAGE, 0.5, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)));
    public static final DeferredHolder<IRefinement, Refinement> N_DAMAGE1 = REFINEMENTS.register("n_damage1", () -> new Refinement(Attributes.ATTACK_DAMAGE, -0.15, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_DAMAGE2 = REFINEMENTS.register("n_damage2", () -> new Refinement(Attributes.ATTACK_DAMAGE, -0.3, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_DAMAGE3 = REFINEMENTS.register("n_damage3", () -> new Refinement(Attributes.ATTACK_DAMAGE, -0.5, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_VALUE)).setDetrimental());

    public static final DeferredHolder<IRefinement, Refinement> ATTACK_SPEED1 = REFINEMENTS.register("attack_speed1", () -> new Refinement(Attributes.ATTACK_SPEED, 0.03, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> ATTACK_SPEED2 = REFINEMENTS.register("attack_speed2", () -> new Refinement(Attributes.ATTACK_SPEED, 0.08, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> ATTACK_SPEED3 = REFINEMENTS.register("attack_speed3", () -> new Refinement(Attributes.ATTACK_SPEED, 0.11, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));
    public static final DeferredHolder<IRefinement, Refinement> N_ATTACK_SPEED1 = REFINEMENTS.register("n_attack_speed1", () -> new Refinement(Attributes.ATTACK_SPEED, -0.05, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_ATTACK_SPEED2 = REFINEMENTS.register("n_attack_speed2", () -> new Refinement(Attributes.ATTACK_SPEED, -0.1, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());
    public static final DeferredHolder<IRefinement, Refinement> N_ATTACK_SPEED3 = REFINEMENTS.register("n_attack_speed3", () -> new Refinement(Attributes.ATTACK_SPEED, -0.15, (uuid, value) -> new AttributeModifier(uuid, value, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).setDetrimental());

    // simple refinements
    public static final DeferredHolder<IRefinement, Refinement> HALF_INVULNERABLE = REFINEMENTS.register("half_invulnerable", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> TELEPORT_DISTANCE = REFINEMENTS.register("teleport_distance", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SWORD_FINISHER = REFINEMENTS.register("sword_finisher", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SUMMON_BATS = REFINEMENTS.register("summon_bats", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> RAGE_FURY = REFINEMENTS.register("rage_fury", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> REGENERATION = REFINEMENTS.register("regeneration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SUN_SCREEN = REFINEMENTS.register("sun_screen", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_PENETRATION = REFINEMENTS.register("dark_blood_projectile_penetration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_MULTI_SHOT = REFINEMENTS.register("dark_blood_projectile_multi_shot", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_AOE = REFINEMENTS.register("dark_blood_projectile_aoe", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_DAMAGE = REFINEMENTS.register("dark_blood_projectile_damage", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> DARK_BLOOD_PROJECTILE_SPEED = REFINEMENTS.register("dark_blood_projectile_speed", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> VISTA = REFINEMENTS.register("vista", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> FREEZE_DURATION = REFINEMENTS.register("freeze_duration", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> BLOOD_CHARGE_SPEED = REFINEMENTS.register("blood_charge_speed", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> SWORD_TRAINED_AMOUNT = REFINEMENTS.register("sword_trained_amount", Refinement::new);
    public static final DeferredHolder<IRefinement, Refinement> CRUCIFIX_RESISTANT = REFINEMENTS.register("crucifix_resistant", Refinement::new);


    static void register(IEventBus bus) {
        REFINEMENTS.register(bus);
    }
}
