package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.world.refinements.Refinement;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FactionRefinements {

    public static final ResourceKey<IRefinement> ARMOR1 = key("armor1");
    public static final ResourceKey<IRefinement> ARMOR2 = key("armor2");
    public static final ResourceKey<IRefinement> ARMOR3 = key("armor3");
    public static final ResourceKey<IRefinement> N_ARMOR1 = key("n_armor1");
    public static final ResourceKey<IRefinement> N_ARMOR2 = key("n_armor2");
    public static final ResourceKey<IRefinement> N_ARMOR3 = key("n_armor3");

    public static final ResourceKey<IRefinement> SPEED1 = key("speed1");
    public static final ResourceKey<IRefinement> SPEED2 = key("speed2");
    public static final ResourceKey<IRefinement> SPEED3 = key("speed3");
    public static final ResourceKey<IRefinement> N_SPEED1 = key("n_speed1");
    public static final ResourceKey<IRefinement> N_SPEED2 = key("n_speed2");
    public static final ResourceKey<IRefinement> N_SPEED3 = key("n_speed3");

    public static final ResourceKey<IRefinement> HEALTH1 = key("health1");
    public static final ResourceKey<IRefinement> HEALTH2 = key("health2");
    public static final ResourceKey<IRefinement> HEALTH3 = key("health3");
    public static final ResourceKey<IRefinement> N_HEALTH1 = key("n_health1");
    public static final ResourceKey<IRefinement> N_HEALTH2 = key("n_health2");
    public static final ResourceKey<IRefinement> N_HEALTH3 = key("n_health3");

    public static final ResourceKey<IRefinement> DAMAGE1 = key("damage1");
    public static final ResourceKey<IRefinement> DAMAGE2 = key("damage2");
    public static final ResourceKey<IRefinement> DAMAGE3 = key("damage3");
    public static final ResourceKey<IRefinement> N_DAMAGE1 = key("n_damage1");
    public static final ResourceKey<IRefinement> N_DAMAGE2 = key("n_damage2");
    public static final ResourceKey<IRefinement> N_DAMAGE3 = key("n_damage3");

    public static final ResourceKey<IRefinement> ATTACK_SPEED1 = key("attack_speed1");
    public static final ResourceKey<IRefinement> ATTACK_SPEED2 = key("attack_speed2");
    public static final ResourceKey<IRefinement> ATTACK_SPEED3 = key("attack_speed3");
    public static final ResourceKey<IRefinement> N_ATTACK_SPEED1 = key("n_attack_speed1");
    public static final ResourceKey<IRefinement> N_ATTACK_SPEED2 = key("n_attack_speed2");
    public static final ResourceKey<IRefinement> N_ATTACK_SPEED3 = key("n_attack_speed3");

    public static void createRefinements(BootstrapContext<IRefinement> context) {
        context.register(ARMOR1, Refinement.attribute(ARMOR1, Attributes.ARMOR, 0.3, AttributeModifier.Operation.ADD_VALUE));
        context.register(ARMOR2, Refinement.attribute(ARMOR2, Attributes.ARMOR, 0.5, AttributeModifier.Operation.ADD_VALUE));
        context.register(ARMOR3, Refinement.attribute(ARMOR3, Attributes.ARMOR, 1, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_ARMOR1, Refinement.attribute(N_ARMOR1, Attributes.ARMOR, -1, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_ARMOR2, Refinement.attribute(N_ARMOR2, Attributes.ARMOR, -2, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_ARMOR3, Refinement.attribute(N_ARMOR3, Attributes.ARMOR, -3, AttributeModifier.Operation.ADD_VALUE));

        context.register(SPEED1, Refinement.attribute(SPEED1, Attributes.MOVEMENT_SPEED, 0.025, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(SPEED2, Refinement.attribute(SPEED2, Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(SPEED3, Refinement.attribute(SPEED3, Attributes.MOVEMENT_SPEED, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_SPEED1, Refinement.attribute(N_SPEED1, Attributes.MOVEMENT_SPEED, -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_SPEED2, Refinement.attribute(N_SPEED2, Attributes.MOVEMENT_SPEED, -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_SPEED3, Refinement.attribute(N_SPEED3, Attributes.MOVEMENT_SPEED, -0.075, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        context.register(HEALTH1, Refinement.attribute(HEALTH1, Attributes.MAX_HEALTH, 0.5, AttributeModifier.Operation.ADD_VALUE));
        context.register(HEALTH2, Refinement.attribute(HEALTH2, Attributes.MAX_HEALTH, 1, AttributeModifier.Operation.ADD_VALUE));
        context.register(HEALTH3, Refinement.attribute(HEALTH3, Attributes.MAX_HEALTH, 1.5, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_HEALTH1, Refinement.attribute(N_HEALTH1, Attributes.MAX_HEALTH, -1, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_HEALTH2, Refinement.attribute(N_HEALTH2, Attributes.MAX_HEALTH, -2, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_HEALTH3, Refinement.attribute(N_HEALTH3, Attributes.MAX_HEALTH, -3, AttributeModifier.Operation.ADD_VALUE));

        context.register(DAMAGE1, Refinement.attribute(DAMAGE1, Attributes.ATTACK_DAMAGE, 0.15, AttributeModifier.Operation.ADD_VALUE));
        context.register(DAMAGE2, Refinement.attribute(DAMAGE2, Attributes.ATTACK_DAMAGE, 0.3, AttributeModifier.Operation.ADD_VALUE));
        context.register(DAMAGE3, Refinement.attribute(DAMAGE3, Attributes.ATTACK_DAMAGE, 0.5, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_DAMAGE1, Refinement.attribute(N_DAMAGE1, Attributes.ATTACK_DAMAGE, -0.15, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_DAMAGE2, Refinement.attribute(N_DAMAGE2, Attributes.ATTACK_DAMAGE, -0.3, AttributeModifier.Operation.ADD_VALUE));
        context.register(N_DAMAGE3, Refinement.attribute(N_DAMAGE3, Attributes.ATTACK_DAMAGE, -0.5, AttributeModifier.Operation.ADD_VALUE));

        context.register(ATTACK_SPEED1, Refinement.attribute(ATTACK_SPEED1, Attributes.ATTACK_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(ATTACK_SPEED2, Refinement.attribute(ATTACK_SPEED2, Attributes.ATTACK_SPEED, 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(ATTACK_SPEED3, Refinement.attribute(ATTACK_SPEED3, Attributes.ATTACK_SPEED, 0.11, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_ATTACK_SPEED1, Refinement.attribute(N_ATTACK_SPEED1, Attributes.ATTACK_SPEED, -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_ATTACK_SPEED2, Refinement.attribute(N_ATTACK_SPEED2, Attributes.ATTACK_SPEED, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        context.register(N_ATTACK_SPEED3, Refinement.attribute(N_ATTACK_SPEED3, Attributes.ATTACK_SPEED, -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    private static ResourceKey<IRefinement> key(String path) {
        return ResourceKey.create(FactionRegistries.Keys.REFINEMENT, FIdentifier.mod(path));
    }
}
