package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.faction.common.core.FactionRefinements;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;

public class ModRefinementSets {

    // Common refinement set
    // attribute modifier
    public static final ResourceKey<IRefinementSet> ARMOR1 = key("armor1");
    public static final ResourceKey<IRefinementSet> HEALTH1 = key("health1");
    public static final ResourceKey<IRefinementSet> SPEED1 = key("speed1");
    public static final ResourceKey<IRefinementSet> ATTACK_SPEED1 = key("attack_speed1");
    public static final ResourceKey<IRefinementSet> DAMAGE1 = key("damage1");
    public static final ResourceKey<IRefinementSet> DAMAGE1_ATTACK_SPEED1_N_ARMOR2 = key("damage1_attack_speed1_n_armor2");
    public static final ResourceKey<IRefinementSet> ARMOR1_HEALTH1_N_ATTACK_SPEED2 = key("armor1_health1_n_attack_speed2");

    //default skill upgrades
    public static final ResourceKey<IRefinementSet> REGENERATION = key("regeneration");
    public static final ResourceKey<IRefinementSet> SWORD_TRAINED_AMOUNT = key("sword_trained_amount");
    public static final ResourceKey<IRefinementSet> BLOOD_CHARGE_SPEED = key("blood_charge_speed");
    public static final ResourceKey<IRefinementSet> FREEZE_DURATION = key("freeze_duration");
    public static final ResourceKey<IRefinementSet> SWORD_FINISHER = key("sword_finisher");

    //buffed skill upgrades
    public static final ResourceKey<IRefinementSet> REGENERATION_BUFFED = key("regeneration_buffed");
    public static final ResourceKey<IRefinementSet> SWORD_TRAINED_AMOUNT_BUFFED = key("sword_trained_amount_buffed");
    public static final ResourceKey<IRefinementSet> BLOOD_CHARGE_SPEED_BUFFED = key("blood_charge_speed_buffed");
    public static final ResourceKey<IRefinementSet> FREEZE_DURATION_BUFFED = key("freeze_duration_buffed");
    public static final ResourceKey<IRefinementSet> SWORD_FINISHER_BUFFED = key("sword_finisher_buffed");

    //de-buffed skill upgrades
    public static final ResourceKey<IRefinementSet> REGENERATION_DEBUFFED = key("regeneration_debuffed");
    public static final ResourceKey<IRefinementSet> SWORD_TRAINED_AMOUNT_DEBUFFED = key("sword_trained_amount_debuffed");
    public static final ResourceKey<IRefinementSet> BLOOD_CHARGE_SPEED_DEBUFFED = key("blood_charge_speed_debuffed");
    public static final ResourceKey<IRefinementSet> FREEZE_DURATION_DEBUFFED = key("freeze_duration_debuffed");
    public static final ResourceKey<IRefinementSet> SWORD_FINISHER_DEBUFFED = key("sword_finisher_debuffed");

    //Uncommon refinement sets
    // attribute modifier
    public static final ResourceKey<IRefinementSet> ARMOR2_N_HEALTH2 = key("armor2_n_health2");
    public static final ResourceKey<IRefinementSet> HEALTH2_N_DAMAGE1 = key("health2_n_damage1");
    public static final ResourceKey<IRefinementSet> ATTACK_SPEED2_N_ARMOR1 = key("attack_speed2_n_armor1");
    public static final ResourceKey<IRefinementSet> DAMAGE2_N_SPEED1 = key("damage2_n_speed1");
    public static final ResourceKey<IRefinementSet> SPEED2_N_DAMAGE1 = key("speed2_n_damage1");

    // better attribute modifier with de-buffs
    public static final ResourceKey<IRefinementSet> ARMOR3_N_HEALTH3 = key("armor3_n_health3");
    public static final ResourceKey<IRefinementSet> HEALTH3_N_DAMAGE2 = key("health3_n_damage2");
    public static final ResourceKey<IRefinementSet> ATTACK_SPEED3_N_ARMOR2 = key("attack_speed3_n_armor2");
    public static final ResourceKey<IRefinementSet> DAMAGE3_N_SPEED2 = key("damage3_n_speed2");
    public static final ResourceKey<IRefinementSet> SPEED3_N_DAMAGE2 = key("speed3_n_damage2");

    //default skill upgrades
    public static final ResourceKey<IRefinementSet> VISTA = key("vista");
    public static final ResourceKey<IRefinementSet> TELEPORT_DISTANCE = key("teleport_distance");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE = key("dark_blood_projectile_damage");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION = key("dark_blood_projectile_penetration");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT = key("dark_blood_projectile_multi_shot");

    //buffed skill upgrades
    public static final ResourceKey<IRefinementSet> VISTA_BUFFED = key("vista_buffed");
    public static final ResourceKey<IRefinementSet> TELEPORT_DISTANCE_BUFFED = key("teleport_distance_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE_BUFFED = key("dark_blood_projectile_damage_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED = key("dark_blood_projectile_penetration_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED = key("dark_blood_projectile_multi_shot_buffed");

    //de-buffed skill upgrades
    public static final ResourceKey<IRefinementSet> VISTA_DEBUFFED = key("vista_debuffed");
    public static final ResourceKey<IRefinementSet> TELEPORT_DISTANCE_DEBUFFED = key("teleport_distance_debuffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE_DEBUFFED = key("dark_blood_projectile_damage_debuffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED = key("dark_blood_projectile_penetration_debuffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED = key("dark_blood_projectile_multi_shot_debuffed");

    // Rare refinement sets
    // attribute modifier
    public static final ResourceKey<IRefinementSet> ARMOR3_N_HEALTH2 = key("armor3_n_health2");
    public static final ResourceKey<IRefinementSet> HEALTH3_N_ARMOR1 = key("health3_n_armor1");
    public static final ResourceKey<IRefinementSet> ATTACK_SPEED3_N_SPEED1 = key("attack_speed3_n_speed1");
    public static final ResourceKey<IRefinementSet> SPEED1_ARMOR1_HEALTH1 = key("speed1_armor1_health1");
    public static final ResourceKey<IRefinementSet> DAMAGE3_N_ARMOR1 = key("damage3_n_armor1");
    public static final ResourceKey<IRefinementSet> SPEED3_N_ATTACK_SPEED1 = key("speed3_n_attack_speed1");
    public static final ResourceKey<IRefinementSet> DAMAGE1_ATTACK_SPEED1 = key("damage1_attack_speed1");

    // default skill upgrades
    public static final ResourceKey<IRefinementSet> HALF_INVULNERABLE = key("half_invulnerable");
    public static final ResourceKey<IRefinementSet> SUMMON_BATS = key("summon_bats");
    public static final ResourceKey<IRefinementSet> SUN_SCREEN = key("sun_screen");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_SPEED = key("dark_blood_projectile_speed");

    // de-buffed skill upgrades
    public static final ResourceKey<IRefinementSet> HALF_INVULNERABLE_DEBUFFED = key("half_invulnerable_debuffed");
    public static final ResourceKey<IRefinementSet> SUMMON_BATS_DEBUFFED = key("summon_bats_debuffed");
    public static final ResourceKey<IRefinementSet> SUN_SCREEN_DEBUFFED = key("sun_screen_debuffed");

    //other
    public static final ResourceKey<IRefinementSet> CRUCIFIX_RESISTANT = key("crucifix_resistant");

    // Epic refinement sets

    // combined skill upgrades
    public static final ResourceKey<IRefinementSet> VAMPIRE_SWORD = key("vampire_sword");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE = key("dark_blood_projectile");

    // skill upgrades
    public static final ResourceKey<IRefinementSet> RAGE_FURY = key("rage_fury");
    public static final ResourceKey<IRefinementSet> SUMMON_BATS_BUFFED = key("summon_bats_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_AOE = key("dark_blood_projectile_aoe");
    public static final ResourceKey<IRefinementSet> HALF_INVULNERABLE_BUFFED = key("half_invulnerable_buffed");
    public static final ResourceKey<IRefinementSet> SUN_SCREEN_BUFFED = key("sun_screen_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_SPEED_BUFFED = key("dark_blood_projectile_speed_buffed");

    // de-buffed skill upgrades
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_AOE_DEBUFFED = key("dark_blood_projectile_aoe_debuffed");
    public static final ResourceKey<IRefinementSet> RAGE_FURY_DEBUFFED = key("rage_fury_debuffed");

    //other
    public static final ResourceKey<IRefinementSet> CRUCIFIX_RESISTANT_SPEED = key("crucifix_resistant_speed");
    public static final ResourceKey<IRefinementSet> CRUCIFIX_RESISTANT_HEALTH = key("crucifix_resistant_health");
    // Legendary refinement sets
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_AOE_BUFFED = key("dark_blood_projectile_aoe_buffed");
    public static final ResourceKey<IRefinementSet> RAGE_FURY_BUFFED = key("rage_fury_buffed");
    public static final ResourceKey<IRefinementSet> DARK_BLOOD_PROJECTILE_SPEED_DOUBLE_BUFFED = key("dark_blood_projectile_speed_double_buffed");

    static void createRefinementSets(BootstrapContext<IRefinementSet> context) {
        // Common refinement set
        {
            // attribute modifier
            register(context, ARMOR1, 0xFFFFFF, FactionRefinements.ARMOR1);
            register(context, HEALTH1, 0xFFFFFF, FactionRefinements.HEALTH1);
            register(context, SPEED1, 0xFFFFFF, FactionRefinements.SPEED1);
            register(context, ATTACK_SPEED1, 0xFFFFFF, FactionRefinements.ATTACK_SPEED1);
            register(context, DAMAGE1, 0xFFFFFF, FactionRefinements.DAMAGE1);
            register(context, DAMAGE1_ATTACK_SPEED1_N_ARMOR2, 0xFFFFFF, FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1, FactionRefinements.N_ARMOR2);
            register(context, ARMOR1_HEALTH1_N_ATTACK_SPEED2, 0xFFFFFF, FactionRefinements.ARMOR1, FactionRefinements.HEALTH1, FactionRefinements.N_ATTACK_SPEED2);

            //default skill upgrades
            register(context, REGENERATION, 0xedbf0b, ModRefinements.REGENERATION);
            register(context, SWORD_TRAINED_AMOUNT, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT);
            register(context, BLOOD_CHARGE_SPEED, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED);
            register(context, FREEZE_DURATION, 0x0bdfed, ModRefinements.FREEZE_DURATION);
            register(context, SWORD_FINISHER, 0xd10106, ModRefinements.SWORD_FINISHER);

            //buffed skill upgrades
            register(context, REGENERATION_BUFFED, 0xedbf0b, ModRefinements.REGENERATION, FactionRefinements.SPEED1);
            register(context, SWORD_TRAINED_AMOUNT_BUFFED, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.DAMAGE1);
            register(context, BLOOD_CHARGE_SPEED_BUFFED, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.ATTACK_SPEED1);
            register(context, FREEZE_DURATION_BUFFED, 0x0bdfed, ModRefinements.FREEZE_DURATION, FactionRefinements.HEALTH1);
            register(context, SWORD_FINISHER_BUFFED, 0xd10106, ModRefinements.SWORD_FINISHER, FactionRefinements.ARMOR1);

            //de-buffed skill upgrades
            register(context, REGENERATION_DEBUFFED, 0xedbf0b, ModRefinements.REGENERATION, FactionRefinements.N_SPEED1);
            register(context, SWORD_TRAINED_AMOUNT_DEBUFFED, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.N_DAMAGE1);
            register(context, BLOOD_CHARGE_SPEED_DEBUFFED, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.N_ATTACK_SPEED1);
            register(context, FREEZE_DURATION_DEBUFFED, 0x0bdfed, ModRefinements.FREEZE_DURATION, FactionRefinements.N_HEALTH1);
            register(context, SWORD_FINISHER_DEBUFFED, 0xd10106, ModRefinements.SWORD_FINISHER, FactionRefinements.N_ARMOR1);
        }

        //Uncommon refinement sets
        {
            // attribute modifier
            register(context, ARMOR2_N_HEALTH2, 0x55FF55, FactionRefinements.ARMOR2, FactionRefinements.N_HEALTH2);
            register(context, HEALTH2_N_DAMAGE1, 0x55FF55, FactionRefinements.HEALTH2, FactionRefinements.N_DAMAGE1);
            register(context, ATTACK_SPEED2_N_ARMOR1, 0x55FF55, FactionRefinements.ATTACK_SPEED2, FactionRefinements.N_ARMOR1);
            register(context, DAMAGE2_N_SPEED1, 0x55FF55, FactionRefinements.DAMAGE2, FactionRefinements.N_SPEED1);
            register(context, SPEED2_N_DAMAGE1, 0x55FF55, FactionRefinements.SPEED2, FactionRefinements.N_DAMAGE1);

            // better attribute modifier with de-buffs
            register(context, ARMOR3_N_HEALTH3, 0x55FF55, FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH3);
            register(context, HEALTH3_N_DAMAGE2, 0x55FF55, FactionRefinements.HEALTH3, FactionRefinements.N_DAMAGE2);
            register(context, ATTACK_SPEED3_N_ARMOR2, 0x55FF55, FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_ARMOR2);
            register(context, DAMAGE3_N_SPEED2, 0x55FF55, FactionRefinements.DAMAGE3, FactionRefinements.N_SPEED3);
            register(context, SPEED3_N_DAMAGE2, 0x55FF55, FactionRefinements.SPEED3, FactionRefinements.N_DAMAGE2);

            //default skill upgrades
            register(context, VISTA, 0x0bafed, ModRefinements.VISTA);
            register(context, TELEPORT_DISTANCE, 0x068755, ModRefinements.TELEPORT_DISTANCE);
            register(context, DARK_BLOOD_PROJECTILE_DAMAGE, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE);
            register(context, DARK_BLOOD_PROJECTILE_PENETRATION, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION);
            register(context, DARK_BLOOD_PROJECTILE_MULTI_SHOT, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            //buffed skill upgrades
            register(context, VISTA_BUFFED, 0x0bafed, ModRefinements.VISTA, FactionRefinements.SPEED1);
            register(context, TELEPORT_DISTANCE_BUFFED, 0x068755, ModRefinements.TELEPORT_DISTANCE, FactionRefinements.SPEED1);
            register(context, DARK_BLOOD_PROJECTILE_DAMAGE_BUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.DAMAGE1);
            register(context, DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.ATTACK_SPEED1);
            register(context, DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.ATTACK_SPEED1);

            //de-buffed skill upgrades
            register(context, VISTA_DEBUFFED, 0x0bafed, ModRefinements.VISTA, FactionRefinements.N_SPEED1);
            register(context, TELEPORT_DISTANCE_DEBUFFED, 0x068755, ModRefinements.TELEPORT_DISTANCE, FactionRefinements.N_SPEED1);
            register(context, DARK_BLOOD_PROJECTILE_DAMAGE_DEBUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.N_DAMAGE1);
            register(context, DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.N_ATTACK_SPEED1);
            register(context, DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.N_ATTACK_SPEED1);
        }

        // Rare refinement sets
        {
            // attribute modifier
            register(context, ARMOR3_N_HEALTH2, 0x5555FF, FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH2);
            register(context, HEALTH3_N_ARMOR1, 0x5555FF, FactionRefinements.HEALTH3, FactionRefinements.N_ARMOR1);
            register(context, ATTACK_SPEED3_N_SPEED1, 0x5555FF, FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_SPEED1);
            register(context, SPEED1_ARMOR1_HEALTH1, 0x5555FF, FactionRefinements.SPEED1, FactionRefinements.ARMOR1, FactionRefinements.HEALTH1);
            register(context, DAMAGE3_N_ARMOR1, 0x5555FF, FactionRefinements.DAMAGE3, FactionRefinements.N_ARMOR2);
            register(context, SPEED3_N_ATTACK_SPEED1, 0x5555FF, FactionRefinements.SPEED3, FactionRefinements.N_ATTACK_SPEED1);
            register(context, DAMAGE1_ATTACK_SPEED1, 0x5555FF, FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1);

            // default skill upgrades
            register(context, HALF_INVULNERABLE, 0xa96db7, ModRefinements.HALF_INVULNERABLE);
            register(context, SUMMON_BATS, 0x8b8a91, ModRefinements.SUMMON_BATS);
            register(context, SUN_SCREEN, 0xedc20b, ModRefinements.SUN_SCREEN);
            register(context, DARK_BLOOD_PROJECTILE_SPEED, 0xeeabcd, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED);

            // de-buffed skill upgrades
            register(context, HALF_INVULNERABLE_DEBUFFED, 0xa96db7, ModRefinements.HALF_INVULNERABLE, FactionRefinements.N_ARMOR1);
            register(context, SUMMON_BATS_DEBUFFED, 0x8b8a91, ModRefinements.SUMMON_BATS, FactionRefinements.N_HEALTH1);
            register(context, SUN_SCREEN_DEBUFFED, 0xedc20b, ModRefinements.SUN_SCREEN, FactionRefinements.N_ARMOR1);

            //other
            register(context, CRUCIFIX_RESISTANT, 0x7DC2D1, ModRefinements.CRUCIFIX_RESISTANT);

        }

        // Epic refinement sets
        {

            // combined skill upgrades
            register(context, VAMPIRE_SWORD, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT, ModRefinements.BLOOD_CHARGE_SPEED);
            register(context, DARK_BLOOD_PROJECTILE, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            // skill upgrades
            register(context, RAGE_FURY, 0xc10202, ModRefinements.RAGE_FURY);
            register(context, SUMMON_BATS_BUFFED, 0x8b8a91, ModRefinements.SUMMON_BATS, FactionRefinements.HEALTH1);
            register(context, DARK_BLOOD_PROJECTILE_AOE, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE);
            register(context, HALF_INVULNERABLE_BUFFED, 0xa96db7, ModRefinements.HALF_INVULNERABLE, FactionRefinements.ARMOR1);
            register(context, SUN_SCREEN_BUFFED, 0xedc20b, ModRefinements.SUN_SCREEN, FactionRefinements.ARMOR1);
            register(context, DARK_BLOOD_PROJECTILE_SPEED_BUFFED, 0xeeab6d, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED1);

            // de-buffed skill upgrades
            register(context, DARK_BLOOD_PROJECTILE_AOE_DEBUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.N_HEALTH1);
            register(context, RAGE_FURY_DEBUFFED, 0xc10202, ModRefinements.RAGE_FURY, FactionRefinements.N_ARMOR1);

            //other
            register(context, CRUCIFIX_RESISTANT_SPEED, 0x7D55D1, ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.ATTACK_SPEED1);
            register(context, CRUCIFIX_RESISTANT_HEALTH, 0x7D55D1, ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.HEALTH2);
        }
        // Legendary refinement sets
        {
            register(context, DARK_BLOOD_PROJECTILE_AOE_BUFFED, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.HEALTH1);
            register(context, RAGE_FURY_BUFFED, 0xc10202, ModRefinements.RAGE_FURY, FactionRefinements.ARMOR1);
            register(context, DARK_BLOOD_PROJECTILE_SPEED_DOUBLE_BUFFED, 0xee5acd, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED3);
        }

    }

    @SafeVarargs
    private static void register(BootstrapContext<IRefinementSet> context, ResourceKey<IRefinementSet> key, int color, ResourceKey<IRefinement>... refinements) {
        HolderGetter<IRefinement> lookup = context.lookup(FactionRegistries.Keys.REFINEMENT);
        context.register(key, RefinementSet.of(key, color, Arrays.stream(refinements).map(lookup::getOrThrow).toList()));
    }

    private static ResourceKey<IRefinementSet> key(String path) {
        return ResourceKey.create(FactionRegistries.Keys.REFINEMENT_SET, VIdentifier.mod(path));
    }
}
