package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.world.refinements.Refinement;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionRefinements {

    public static final DeferredRegister<IRefinement> REFINEMENTS = DeferredRegister.create(FactionRegistries.Keys.REFINEMENT, REFERENCE.MOD_ID);

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

    static void register(IEventBus bus) {
        REFINEMENTS.register(bus);
    }

}
