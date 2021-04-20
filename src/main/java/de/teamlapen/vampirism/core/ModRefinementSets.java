package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet.Rarity;

@ObjectHolder(REFERENCE.MODID)
public class ModRefinementSets {

    public static void registerRefinementSets(IForgeRegistry<IRefinementSet> registry) {//TODO think of cool registry names
        // Common refinement set
        {
            // attribute modifier
            registry.register(commonV("armor1", ModRefinements.armor1));
            registry.register(commonV("health1", ModRefinements.health1));
            registry.register(commonV("speed1", ModRefinements.speed1));
            registry.register(commonV("attack_speed1", ModRefinements.attack_speed1));
            registry.register(commonV("damage1", ModRefinements.damage1));
            registry.register(commonV("damage1_attack_speed1_n_armor2", ModRefinements.damage1, ModRefinements.attack_speed1, ModRefinements.n_armor2));
            registry.register(commonV("armor1_health1_n_attack_speed2", ModRefinements.armor1, ModRefinements.health1, ModRefinements.n_attack_speed2));

            //default skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration).setRegistryName(REFERENCE.MODID, "regeneration"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount).setRegistryName(REFERENCE.MODID, "sword_trained_amount"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed).setRegistryName(REFERENCE.MODID, "blood_charge_speed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration).setRegistryName(REFERENCE.MODID, "freeze_duration"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher).setRegistryName(REFERENCE.MODID, "sword_finisher"));

            //buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration, ModRefinements.speed1).setRegistryName(REFERENCE.MODID, "regeneration_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.damage1).setRegistryName(REFERENCE.MODID, "sword_trained_amount_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed, ModRefinements.attack_speed1).setRegistryName(REFERENCE.MODID, "blood_charge_speed_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration, ModRefinements.health1).setRegistryName(REFERENCE.MODID, "freeze_duration_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher, ModRefinements.armor1).setRegistryName(REFERENCE.MODID, "sword_finisher_buffed"));

            //de-buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.regeneration, ModRefinements.n_speed1).setRegistryName(REFERENCE.MODID, "regeneration_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.n_damage1).setRegistryName(REFERENCE.MODID, "sword_trained_amount_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.blood_charge_speed, ModRefinements.n_attack_speed1).setRegistryName(REFERENCE.MODID, "blood_charge_speed_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.freeze_duration, ModRefinements.n_health1).setRegistryName(REFERENCE.MODID, "freeze_duration_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.sword_finisher, ModRefinements.n_armor1).setRegistryName(REFERENCE.MODID,"sword_finisher_debuffed" ));
        }

        //Uncommon refinement sets
        {
            // attribute modifier
            registry.register(uncommonV("armor2_n_health2", ModRefinements.armor2, ModRefinements.n_health2));
            registry.register(uncommonV("health2_n_damage1", ModRefinements.health2, ModRefinements.n_damage1));
            registry.register(uncommonV("attack_speed2_n_armor1", ModRefinements.attack_speed2, ModRefinements.n_armor1));
            registry.register(uncommonV("damage2_n_speed1", ModRefinements.damage2, ModRefinements.n_speed1));
            registry.register(uncommonV("speed2_n_damage1", ModRefinements.speed2, ModRefinements.n_damage1));

            // better attribute modifier with de-buffs
            registry.register(uncommonV("armor3_n_health3", ModRefinements.armor3, ModRefinements.n_health3));
            registry.register(uncommonV("health3_n_damage2", ModRefinements.health3, ModRefinements.n_damage2));
            registry.register(uncommonV("attack_speed3_n_armor2", ModRefinements.attack_speed3, ModRefinements.n_armor2));
            registry.register(uncommonV("damage3_n_speed2", ModRefinements.damage3, ModRefinements.n_speed2));
            registry.register(uncommonV("speed3_n_damage2", ModRefinements.speed3, ModRefinements.n_damage2));

            //default skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista).setRegistryName(REFERENCE.MODID, "vista"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance).setRegistryName(REFERENCE.MODID, "teleport_distance"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_damage"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_penetration"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_multi_shot"));

            //buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista, ModRefinements.speed1).setRegistryName(REFERENCE.MODID, "vista_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance, ModRefinements.speed1).setRegistryName(REFERENCE.MODID, "teleport_distance_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage, ModRefinements.damage1).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_damage_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_penetration_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot, ModRefinements.attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_multi_shot_buffed"));

            //de-buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.vista, ModRefinements.n_speed1).setRegistryName(REFERENCE.MODID, "vista_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.teleport_distance, ModRefinements.n_speed1).setRegistryName(REFERENCE.MODID, "teleport_distance_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_damage, ModRefinements.n_damage1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_damage_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.n_attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_penetration_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.dark_blood_projectile_multi_shot, ModRefinements.n_attack_speed1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID,"dark_blood_projectile_multi_shot_debuffed" ));
        }

        // Rare refinement sets
        {
            // attribute modifier
            registry.register(rareV("armor3_n_health2", ModRefinements.armor3, ModRefinements.n_health2));
            registry.register(rareV("health3_n_armor1", ModRefinements.health3, ModRefinements.n_armor1));
            registry.register(rareV("attack_speed3_n_speed1", ModRefinements.attack_speed3, ModRefinements.n_speed1));
            registry.register(rareV("speed1_armor1_health1", ModRefinements.speed1, ModRefinements.armor1, ModRefinements.health1));
            registry.register(rareV("damage3_n_armor1", ModRefinements.damage3, ModRefinements.n_armor1));
            registry.register(rareV("speed3_n_attack_speed1", ModRefinements.speed3, ModRefinements.n_attack_speed1));
            registry.register(rareV("damage1_attack_speed1", ModRefinements.damage1, ModRefinements.attack_speed1));

            // default skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.half_invulnerable).setRegistryName(REFERENCE.MODID, "half_invulnerable"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.summon_bats).setRegistryName(REFERENCE.MODID, "summon_bats"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.sun_screen).setRegistryName(REFERENCE.MODID, "sun_screen"));

            // de-buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.half_invulnerable, ModRefinements.n_armor1).setRegistryName(REFERENCE.MODID, "half_invulnerable_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.summon_bats, ModRefinements.n_health1).setRegistryName(REFERENCE.MODID, "summon_bats_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.sun_screen, ModRefinements.n_armor1).setRegistryName(REFERENCE.MODID,"sun_screen_debuffed" ));
        }

        // Epic refinement sets
        {

            // combined skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xed3b0b, ModRefinements.sword_trained_amount, ModRefinements.blood_charge_speed).setRegistryName(REFERENCE.MODID,"vampire_sword" ));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_penetration, ModRefinements.dark_blood_projectile_multi_shot).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID,"dark_blood_projectile" ));

            // skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xc10202, ModRefinements.rage_fury).setRegistryName(REFERENCE.MODID, "rage_fury"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0x8b8a91, ModRefinements.summon_bats, ModRefinements.health1).setRegistryName(REFERENCE.MODID, "summon_bats_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_aoe).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_aoe"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xa96db7, ModRefinements.half_invulnerable, ModRefinements.armor1).setRegistryName(REFERENCE.MODID, "half_invulnerable_buffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xedc20b, ModRefinements.sun_screen, ModRefinements.armor1).setRegistryName(REFERENCE.MODID, "sun_screen_buffed"));

            // de-buffed skill upgrades
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.dark_blood_projectile_aoe, ModRefinements.n_health1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "dark_blood_projectile_aoe_debuffed"));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xc10202, ModRefinements.rage_fury, ModRefinements.n_armor1).setRegistryName(REFERENCE.MODID, "rage_fury_debuffed"));
        }
        // Legendary refinement sets
        {
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xe303ff, ModRefinements.dark_blood_projectile_aoe, ModRefinements.health1).onlyFor(IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID,"dark_blood_projectile_aoe_buffed" ));
            registry.register(new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xc10202, ModRefinements.rage_fury, ModRefinements.armor1).setRegistryName(REFERENCE.MODID,"rage_fury_buffed" ));
        }
    }

    private static IRefinementSet commonV(String name, IRefinement... refinements) {
        return vampire(Rarity.COMMON, refinements).setRegistryName(REFERENCE.MODID, name);
    }

    private static IRefinementSet uncommonV(String name, IRefinement... refinements) {
        return vampire(Rarity.UNCOMMON, refinements).setRegistryName(REFERENCE.MODID, name);
    }

    private static IRefinementSet rareV(String name, IRefinement... refinements) {
        return vampire(Rarity.RARE, refinements).setRegistryName(REFERENCE.MODID, name);
    }

    private static IRefinementSet epicV(String name, IRefinement... refinements) {
        return vampire(Rarity.EPIC, refinements).setRegistryName(REFERENCE.MODID, name);
    }

    @SuppressWarnings("ConstantConditions")
    private static RefinementSet vampire(Rarity rarity, IRefinement... refinements) {
        return new RefinementSet.VampireRefinementSet(rarity, rarity.color.getColor(), refinements);
    }
}
