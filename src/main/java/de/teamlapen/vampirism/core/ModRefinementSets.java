package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet.Rarity;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRefinementSets {
    public static final DeferredRegister<IRefinementSet> REFINEMENT_SETS = DeferredRegister.create(ModRegistries.REFINEMENT_SET_ID, REFERENCE.MODID);

    public static void registerRefinementSets(IEventBus bus) {
        REFINEMENT_SETS.register(bus);
    }
    
    static {
        // Common refinement set
        {
            // attribute modifier
            REFINEMENT_SETS.register("armor1", () -> commonV(ModRefinements.armor1));
            REFINEMENT_SETS.register("health1", () -> commonV(ModRefinements.health1));
            REFINEMENT_SETS.register("speed1", () -> commonV(ModRefinements.speed1));
            REFINEMENT_SETS.register("attack_speed1", () -> commonV(ModRefinements.attack_speed1));
            REFINEMENT_SETS.register("damage1", () -> commonV(ModRefinements.damage1));
            REFINEMENT_SETS.register("damage1_attack_speed1_n_armor2", () -> commonV(ModRefinements.damage1, ModRefinements.attack_speed1, ModRefinements.n_armor2));
            REFINEMENT_SETS.register("armor1_health1_n_attack_speed2", () -> commonV(ModRefinements.armor1, ModRefinements.health1, ModRefinements.n_attack_speed2));

            //default skill upgrades
            REFINEMENT_SETS.register("regeneration", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration));
            REFINEMENT_SETS.register("sword_trained_amount", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount));
            REFINEMENT_SETS.register("blood_charge_speed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed));
            REFINEMENT_SETS.register("freeze_duration", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration));
            REFINEMENT_SETS.register("sword_finisher", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher));

            //buffed skill upgrades
            REFINEMENT_SETS.register("regeneration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration, ModRefinements.speed1));
            REFINEMENT_SETS.register("sword_trained_amount_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.damage1));
            REFINEMENT_SETS.register("blood_charge_speed_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed, ModRefinements.attack_speed1));
            REFINEMENT_SETS.register("freeze_duration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration, ModRefinements.health1));
            REFINEMENT_SETS.register("sword_finisher_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher, ModRefinements.armor1));

            //de-buffed skill upgrades
            REFINEMENT_SETS.register("regeneration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration, ModRefinements.n_speed1));
            REFINEMENT_SETS.register("sword_trained_amount_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.n_damage1));
            REFINEMENT_SETS.register("blood_charge_speed_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed, ModRefinements.n_attack_speed1));
            REFINEMENT_SETS.register("freeze_duration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration, ModRefinements.n_health1));
            REFINEMENT_SETS.register("sword_finisher_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher, ModRefinements.n_armor1));
        }

        //Uncommon refinement sets
        {
            // attribute modifier
            REFINEMENT_SETS.register("armor2_n_health2", () -> uncommonV(ModRefinements.armor2, ModRefinements.n_health2));
            REFINEMENT_SETS.register("health2_n_damage1", () -> uncommonV(ModRefinements.health2, ModRefinements.n_damage1));
            REFINEMENT_SETS.register("attack_speed2_n_armor1", () -> uncommonV(ModRefinements.attack_speed2, ModRefinements.n_armor1));
            REFINEMENT_SETS.register("damage2_n_speed1", () -> uncommonV(ModRefinements.damage2, ModRefinements.n_speed1));
            REFINEMENT_SETS.register("speed2_n_damage1", () -> uncommonV(ModRefinements.speed2, ModRefinements.n_damage1));

            // better attribute modifier with de-buffs
            REFINEMENT_SETS.register("armor3_n_health3", () -> uncommonV(ModRefinements.armor3, ModRefinements.n_health3));
            REFINEMENT_SETS.register("health3_n_damage2", () -> uncommonV(ModRefinements.health3, ModRefinements.n_damage2));
            REFINEMENT_SETS.register("attack_speed3_n_armor2", () -> uncommonV(ModRefinements.attack_speed3, ModRefinements.n_armor2));
            REFINEMENT_SETS.register("damage3_n_speed2", () -> uncommonV(ModRefinements.damage3, ModRefinements.n_speed3));
            REFINEMENT_SETS.register("speed3_n_damage2", () -> uncommonV(ModRefinements.speed3, ModRefinements.n_damage2));

            //default skill upgrades
            REFINEMENT_SETS.register("vista", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista));
            REFINEMENT_SETS.register("teleport_distance", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance));
            REFINEMENT_SETS.register("dark_blood_projectile_damage", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage));
            REFINEMENT_SETS.register("dark_blood_projectile_penetration", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot).onlyFor(IRefinementItem.AccessorySlotType.RING));

            //buffed skill upgrades
            REFINEMENT_SETS.register("vista_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista, ModRefinements.speed1));
            REFINEMENT_SETS.register("teleport_distance_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance, ModRefinements.speed1));
            REFINEMENT_SETS.register("dark_blood_projectile_damage_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage, ModRefinements.damage1));
            REFINEMENT_SETS.register("dark_blood_projectile_penetration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot, ModRefinements.attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING));

            //de-buffed skill upgrades
            REFINEMENT_SETS.register("vista_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista, ModRefinements.n_speed1));
            REFINEMENT_SETS.register("teleport_distance_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance, ModRefinements.n_speed1));
            REFINEMENT_SETS.register("dark_blood_projectile_damage_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage, ModRefinements.n_damage1));
            REFINEMENT_SETS.register("dark_blood_projectile_penetration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.n_attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot, ModRefinements.n_attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING));
        }

        // Rare refinement sets
        {
            // attribute modifier
            REFINEMENT_SETS.register("armor3_n_health2", () -> rareV(ModRefinements.armor3, ModRefinements.n_health2));
            REFINEMENT_SETS.register("health3_n_armor1", () -> rareV(ModRefinements.health3, ModRefinements.n_armor1));
            REFINEMENT_SETS.register("attack_speed3_n_speed1", () -> rareV(ModRefinements.attack_speed3, ModRefinements.n_speed1));
            REFINEMENT_SETS.register("speed1_armor1_health1", () -> rareV(ModRefinements.speed1, ModRefinements.armor1, ModRefinements.health1));
            REFINEMENT_SETS.register("damage3_n_armor1", () -> rareV(ModRefinements.damage3, ModRefinements.n_armor2));
            REFINEMENT_SETS.register("speed3_n_attack_speed1", () -> rareV(ModRefinements.speed3, ModRefinements.n_attack_speed1));
            REFINEMENT_SETS.register("damage1_attack_speed1", () -> rareV(ModRefinements.damage1, ModRefinements.attack_speed1));

            // default skill upgrades
            REFINEMENT_SETS.register("half_invulnerable", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.half_invulnerable));
            REFINEMENT_SETS.register("summon_bats", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.summon_bats));
            REFINEMENT_SETS.register("sun_screen", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.sun_screen));
            REFINEMENT_SETS.register("dark_blood_projectile_speed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xeeabcd, ModRefinements.dark_blood_projectile_speed));

            // de-buffed skill upgrades
            REFINEMENT_SETS.register("half_invulnerable_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.half_invulnerable, ModRefinements.n_armor1));
            REFINEMENT_SETS.register("summon_bats_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.summon_bats, ModRefinements.n_health1));
            REFINEMENT_SETS.register("sun_screen_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.sun_screen, ModRefinements.n_armor1));
        }

        // Epic refinement sets
        {

            // combined skill upgrades
            REFINEMENT_SETS.register("vampire_sword", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.blood_charge_speed));
            REFINEMENT_SETS.register("dark_blood_projectile", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.dark_blood_projectile_multi_shot).onlyFor(IRefinementItem.AccessorySlotType.RING));

            // skill upgrades
            REFINEMENT_SETS.register("rage_fury", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xc10202, ModRefinements.rage_fury));
            REFINEMENT_SETS.register("summon_bats_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0x8b8a91, ModRefinements.summon_bats, ModRefinements.health1));
            REFINEMENT_SETS.register("dark_blood_projectile_aoe", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_aoe).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("half_invulnerable_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xa96db7, ModRefinements.half_invulnerable, ModRefinements.armor1));
            REFINEMENT_SETS.register("sun_screen_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xedc20b, ModRefinements.sun_screen, ModRefinements.armor1));
            REFINEMENT_SETS.register("dark_blood_projectile_speed_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xeeab6d, ModRefinements.dark_blood_projectile_speed, ModRefinements.speed1));

            // de-buffed skill upgrades
            REFINEMENT_SETS.register("dark_blood_projectile_aoe_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_aoe, ModRefinements.n_health1).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("rage_fury_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xc10202, ModRefinements.rage_fury, ModRefinements.n_armor1));
        }
        // Legendary refinement sets
        {
            REFINEMENT_SETS.register("dark_blood_projectile_aoe_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xe303ff, ModRefinements.dark_blood_projectile_aoe, ModRefinements.health1).onlyFor(IRefinementItem.AccessorySlotType.RING));
            REFINEMENT_SETS.register("rage_fury_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xc10202, ModRefinements.rage_fury, ModRefinements.armor1));
            REFINEMENT_SETS.register("dark_blood_projectile_speed_double_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xee5acd, ModRefinements.dark_blood_projectile_speed, ModRefinements.speed3));
        }
    }

    @SafeVarargs
    private static IRefinementSet commonV(RegistryObject<? extends IRefinement>... refinements) {
        return vampire(Rarity.COMMON, refinements);
    }

    @SafeVarargs
    private static IRefinementSet uncommonV(RegistryObject<? extends IRefinement>... refinements) {
        return vampire(Rarity.UNCOMMON, refinements);
    }

    @SafeVarargs
    private static IRefinementSet rareV(RegistryObject<? extends IRefinement>... refinements) {
        return vampire(Rarity.RARE, refinements);
    }

    @SafeVarargs
    private static IRefinementSet epicV(RegistryObject<? extends IRefinement>... refinements) {
        return vampire(Rarity.EPIC, refinements);
    }

    @SuppressWarnings("ConstantConditions")
    @SafeVarargs
    private static RefinementSet vampire(Rarity rarity, RegistryObject<? extends IRefinement>... refinements) {
        return new RefinementSet.VampireRefinementSet(rarity, rarity.color.getColor(), refinements);
    }
}
