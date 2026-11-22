package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet.Rarity;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.faction.common.core.FactionRefinements;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;

public class ModRefinementSets {

    static void createRefinementSets(BootstrapContext<IRefinementSet> context) {
        // Common refinement set
        {
            // attribute modifier
            register(context, "armor1", vampire(Rarity.COMMON), FactionRefinements.ARMOR1);
            register(context, "health1", vampire(Rarity.COMMON), FactionRefinements.HEALTH1);
            register(context, "speed1", vampire(Rarity.COMMON), FactionRefinements.SPEED1);
            register(context, "attack_speed1", vampire(Rarity.COMMON), FactionRefinements.ATTACK_SPEED1);
            register(context, "damage1", vampire(Rarity.COMMON), FactionRefinements.DAMAGE1);
            register(context, "damage1_attack_speed1_n_armor2", vampire(Rarity.COMMON), FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1, FactionRefinements.N_ARMOR2);
            register(context, "armor1_health1_n_attack_speed2", vampire(Rarity.COMMON), FactionRefinements.ARMOR1, FactionRefinements.HEALTH1, FactionRefinements.N_ATTACK_SPEED2);

            //default skill upgrades
            register(context, "regeneration", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION);
            register(context, "sword_trained_amount", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT);
            register(context, "blood_charge_speed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED);
            register(context, "freeze_duration", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION);
            register(context, "sword_finisher", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER);

            //buffed skill upgrades
            register(context, "regeneration_buffed", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION, FactionRefinements.SPEED1);
            register(context, "sword_trained_amount_buffed", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.DAMAGE1);
            register(context, "blood_charge_speed_buffed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.ATTACK_SPEED1);
            register(context, "freeze_duration_buffed", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION, FactionRefinements.HEALTH1);
            register(context, "sword_finisher_buffed", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER, FactionRefinements.ARMOR1);

            //de-buffed skill upgrades
            register(context, "regeneration_debuffed", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION, FactionRefinements.N_SPEED1);
            register(context, "sword_trained_amount_debuffed", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.N_DAMAGE1);
            register(context, "blood_charge_speed_debuffed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.N_ATTACK_SPEED1);
            register(context, "freeze_duration_debuffed", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION, FactionRefinements.N_HEALTH1);
            register(context, "sword_finisher_debuffed", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER, FactionRefinements.N_ARMOR1);
        }

        //Uncommon refinement sets
        {
            // attribute modifier
            register(context, "armor2_n_health2", vampire(Rarity.UNCOMMON), FactionRefinements.ARMOR2, FactionRefinements.N_HEALTH2);
            register(context, "health2_n_damage1", vampire(Rarity.UNCOMMON), FactionRefinements.HEALTH2, FactionRefinements.N_DAMAGE1);
            register(context, "attack_speed2_n_armor1", vampire(Rarity.UNCOMMON), FactionRefinements.ATTACK_SPEED2, FactionRefinements.N_ARMOR1);
            register(context, "damage2_n_speed1", vampire(Rarity.UNCOMMON), FactionRefinements.DAMAGE2, FactionRefinements.N_SPEED1);
            register(context, "speed2_n_damage1", vampire(Rarity.UNCOMMON), FactionRefinements.SPEED2, FactionRefinements.N_DAMAGE1);

            // better attribute modifier with de-buffs
            register(context, "armor3_n_health3", vampire(Rarity.UNCOMMON), FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH3);
            register(context, "health3_n_damage2", vampire(Rarity.UNCOMMON), FactionRefinements.HEALTH3, FactionRefinements.N_DAMAGE2);
            register(context, "attack_speed3_n_armor2", vampire(Rarity.UNCOMMON), FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_ARMOR2);
            register(context, "damage3_n_speed2", vampire(Rarity.UNCOMMON), FactionRefinements.DAMAGE3, FactionRefinements.N_SPEED3);
            register(context, "speed3_n_damage2", vampire(Rarity.UNCOMMON), FactionRefinements.SPEED3, FactionRefinements.N_DAMAGE2);

            //default skill upgrades
            register(context, "vista", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA);
            register(context, "teleport_distance", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE);
            register(context, "dark_blood_projectile_damage", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE);
            register(context, "dark_blood_projectile_penetration", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION);
            register(context, "dark_blood_projectile_multi_shot", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            //buffed skill upgrades
            register(context, "vista_buffed", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA, FactionRefinements.SPEED1);
            register(context, "teleport_distance_buffed", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE, FactionRefinements.SPEED1);
            register(context, "dark_blood_projectile_damage_buffed", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.DAMAGE1);
            register(context, "dark_blood_projectile_penetration_buffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.ATTACK_SPEED1);
            register(context, "dark_blood_projectile_multi_shot_buffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.ATTACK_SPEED1);

            //de-buffed skill upgrades
            register(context, "vista_debuffed", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA, FactionRefinements.N_SPEED1);
            register(context, "teleport_distance_debuffed", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE, FactionRefinements.N_SPEED1);
            register(context, "dark_blood_projectile_damage_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.N_DAMAGE1);
            register(context, "dark_blood_projectile_penetration_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.N_ATTACK_SPEED1);
            register(context, "dark_blood_projectile_multi_shot_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.N_ATTACK_SPEED1);
        }

        // Rare refinement sets
        {
            // attribute modifier
            register(context, "armor3_n_health2", vampire(Rarity.RARE), FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH2);
            register(context, "health3_n_armor1", vampire(Rarity.RARE), FactionRefinements.HEALTH3, FactionRefinements.N_ARMOR1);
            register(context, "attack_speed3_n_speed1", vampire(Rarity.RARE), FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_SPEED1);
            register(context, "speed1_armor1_health1", vampire(Rarity.RARE), FactionRefinements.SPEED1, FactionRefinements.ARMOR1, FactionRefinements.HEALTH1);
            register(context, "damage3_n_armor1", vampire(Rarity.RARE), FactionRefinements.DAMAGE3, FactionRefinements.N_ARMOR2);
            register(context, "speed3_n_attack_speed1", vampire(Rarity.RARE), FactionRefinements.SPEED3, FactionRefinements.N_ATTACK_SPEED1);
            register(context, "damage1_attack_speed1", vampire(Rarity.RARE), FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1);

            // default skill upgrades
            register(context, "half_invulnerable", vampire(Rarity.RARE, 0xa96db7), ModRefinements.HALF_INVULNERABLE);
            register(context, "summon_bats", vampire(Rarity.RARE, 0x8b8a91), ModRefinements.SUMMON_BATS);
            register(context, "sun_screen", vampire(Rarity.RARE, 0xedc20b), ModRefinements.SUN_SCREEN);
            register(context, "dark_blood_projectile_speed", vampire(Rarity.RARE, 0xeeabcd), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED);

            // de-buffed skill upgrades
            register(context, "half_invulnerable_debuffed", vampire(Rarity.RARE, 0xa96db7), ModRefinements.HALF_INVULNERABLE, FactionRefinements.N_ARMOR1);
            register(context, "summon_bats_debuffed", vampire(Rarity.RARE, 0x8b8a91), ModRefinements.SUMMON_BATS, FactionRefinements.N_HEALTH1);
            register(context, "sun_screen_debuffed", vampire(Rarity.RARE, 0xedc20b), ModRefinements.SUN_SCREEN, FactionRefinements.N_ARMOR1);

            //other
            register(context, "crucifix_resistant", vampire(Rarity.UNCOMMON, 0x7DC2D1), ModRefinements.CRUCIFIX_RESISTANT);

        }

        // Epic refinement sets
        {

            // combined skill upgrades
            register(context, "vampire_sword", vampire(Rarity.EPIC, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, ModRefinements.BLOOD_CHARGE_SPEED);
            register(context, "dark_blood_projectile", vampire(Rarity.EPIC, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            // skill upgrades
            register(context, "rage_fury", vampire(Rarity.EPIC, 0xc10202), ModRefinements.RAGE_FURY);
            register(context, "summon_bats_buffed", vampire(Rarity.EPIC, 0x8b8a91), ModRefinements.SUMMON_BATS, FactionRefinements.HEALTH1);
            register(context, "dark_blood_projectile_aoe", vampire(Rarity.EPIC, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE);
            register(context, "half_invulnerable_buffed", vampire(Rarity.EPIC, 0xa96db7), ModRefinements.HALF_INVULNERABLE, FactionRefinements.ARMOR1);
            register(context, "sun_screen_buffed", vampire(Rarity.EPIC, 0xedc20b), ModRefinements.SUN_SCREEN, FactionRefinements.ARMOR1);
            register(context, "dark_blood_projectile_speed_buffed", vampire(Rarity.EPIC, 0xeeab6d), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED1);

            // de-buffed skill upgrades
            register(context, "dark_blood_projectile_aoe_debuffed", vampire(Rarity.EPIC, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.N_HEALTH1);
            register(context, "rage_fury_debuffed", vampire(Rarity.EPIC, 0xc10202), ModRefinements.RAGE_FURY, FactionRefinements.N_ARMOR1);

            //other
            register(context, "crucifix_resistant_speed", vampire(Rarity.EPIC, 0x7D55D1), ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.ATTACK_SPEED1);
            register(context, "crucifix_resistant_health", vampire(Rarity.EPIC, 0x7D55D1), ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.HEALTH2);
        }
        // Legendary refinement sets
        {
            register(context, "dark_blood_projectile_aoe_buffed", vampire(Rarity.LEGENDARY, 0xe303ff).onlyFor(IRefinementItem.AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.HEALTH1);
            register(context, "rage_fury_buffed", vampire(Rarity.LEGENDARY, 0xc10202), ModRefinements.RAGE_FURY, FactionRefinements.ARMOR1);
            register(context, "dark_blood_projectile_speed_double_buffed", vampire(Rarity.LEGENDARY, 0xee5acd), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED3);
        }
    }

    @SafeVarargs
    private static void register(BootstrapContext<IRefinementSet> context, String name, RefinementSet.Builder builder, ResourceKey<IRefinement>... refinements) {
        HolderGetter<IRefinement> lookup = context.lookup(FactionRegistries.Keys.REFINEMENT);
        ResourceKey<IRefinementSet> key = ResourceKey.create(FactionRegistries.Keys.REFINEMENT_SET, VIdentifier.mod(name));
        context.register(key, builder.build(key, Arrays.stream(refinements).map(lookup::getOrThrow).toList()));
    }

    private static RefinementSet.Builder vampire(Rarity rarity) {
        //noinspection DataFlowIssue
        return vampire(rarity, rarity.color.getColor());
    }

    private static RefinementSet.Builder vampire(Rarity rarity, int color) {
        return RefinementSet.builder(VampirismTags.Factions.IS_VAMPIRE, rarity, color);
    }
}
