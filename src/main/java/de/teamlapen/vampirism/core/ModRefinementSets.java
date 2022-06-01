package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet.Rarity;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModRefinementSets {
    public static final DeferredRegister<IRefinementSet> REFINEMENT_SETS = DeferredRegister.create(ModRegistries.REFINEMENT_SETS, REFERENCE.MODID);

    // Common refinement set
    // attribute modifier
    public static final RegistryObject<RefinementSet> ARMOR1 =
            REFINEMENT_SETS.register("armor1", () -> commonV(ModRefinements.ARMOR1.get()));
    public static final RegistryObject<RefinementSet> HEALTH1 =
            REFINEMENT_SETS.register("health1", () -> commonV(ModRefinements.HEALTH1.get()));
    public static final RegistryObject<RefinementSet> SPEED1 =
            REFINEMENT_SETS.register("speed1", () -> commonV(ModRefinements.SPEED1.get()));
    public static final RegistryObject<RefinementSet> ATTACK_SPEED1 =
            REFINEMENT_SETS.register("attack_speed1", () -> commonV(ModRefinements.ATTACK_SPEED1.get()));
    public static final RegistryObject<RefinementSet> DAMAGE1 =
            REFINEMENT_SETS.register("damage1", () -> commonV(ModRefinements.DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> DAMAGE1_ATTACK_SPEED1_N_ARMOR2 =
            REFINEMENT_SETS.register("damage1_attack_speed1_n_armor2", () -> commonV(ModRefinements.DAMAGE1.get(), ModRefinements.ATTACK_SPEED1.get(), ModRefinements.N_ARMOR2.get()));
    public static final RegistryObject<RefinementSet> ARMOR1_HEALTH1_N_ATTACK_SPEED2 =
            REFINEMENT_SETS.register("armor1_health1_n_attack_speed2", () -> commonV(ModRefinements.ARMOR1.get(), ModRefinements.HEALTH1.get(), ModRefinements.N_ATTACK_SPEED2.get()));

    //default skill upgrades
    public static final RegistryObject<RefinementSet> REGENERATION =
            REFINEMENT_SETS.register("regeneration", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.REGENERATION.get()));
    public static final RegistryObject<RefinementSet> SWORD_TRAINED_AMOUNT =
            REFINEMENT_SETS.register("sword_trained_amount", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT.get()));
    public static final RegistryObject<RefinementSet> BLOOD_CHARGE_SPEED =
            REFINEMENT_SETS.register("blood_charge_speed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED.get()));
    public static final RegistryObject<RefinementSet> FREEZE_DURATION =
            REFINEMENT_SETS.register("freeze_duration", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.FREEZE_DURATION.get()));
    public static final RegistryObject<RefinementSet> SWORD_FINISHER =
            REFINEMENT_SETS.register("sword_finisher", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.SWORD_FINISHER.get()));

    //buffed skill upgrades
    public static final RegistryObject<RefinementSet> REGENERATION_BUFFED =
            REFINEMENT_SETS.register("regeneration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.REGENERATION.get(), ModRefinements.SPEED1.get()));
    public static final RegistryObject<RefinementSet> SWORD_TRAINED_AMOUNT_BUFFED =
            REFINEMENT_SETS.register("sword_trained_amount_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT.get(), ModRefinements.DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> BLOOD_CHARGE_SPEED_BUFFED =
            REFINEMENT_SETS.register("blood_charge_speed_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED.get(), ModRefinements.ATTACK_SPEED1.get()));
    public static final RegistryObject<RefinementSet> FREEZE_DURATION_BUFFED =
            REFINEMENT_SETS.register("freeze_duration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.FREEZE_DURATION.get(), ModRefinements.HEALTH1.get()));
    public static final RegistryObject<RefinementSet> SWORD_FINISHER_BUFFED =
            REFINEMENT_SETS.register("sword_finisher_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.SWORD_FINISHER.get(), ModRefinements.ARMOR1.get()));

    //de-buffed skill upgrades
    public static final RegistryObject<RefinementSet> REGENERATION_DEBUFFED =
            REFINEMENT_SETS.register("regeneration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xedbf0b, ModRefinements.REGENERATION.get(), ModRefinements.N_SPEED1.get()));
    public static final RegistryObject<RefinementSet> SWORD_TRAINED_AMOUNT_DEBUFFED =
            REFINEMENT_SETS.register("sword_trained_amount_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT.get(), ModRefinements.N_DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> BLOOD_CHARGE_SPEED_DEBUFFED =
            REFINEMENT_SETS.register("blood_charge_speed_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xed250b, ModRefinements.BLOOD_CHARGE_SPEED.get(), ModRefinements.N_ATTACK_SPEED1.get()));
    public static final RegistryObject<RefinementSet> FREEZE_DURATION_DEBUFFED =
            REFINEMENT_SETS.register("freeze_duration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0x0bdfed, ModRefinements.FREEZE_DURATION.get(), ModRefinements.N_HEALTH1.get()));
    public static final RegistryObject<RefinementSet> SWORD_FINISHER_DEBUFFED =
            REFINEMENT_SETS.register("sword_finisher_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.COMMON, 0xd10106, ModRefinements.SWORD_FINISHER.get(), ModRefinements.N_ARMOR1.get()));

    //Uncommon refinement sets
    // attribute modifier
    public static final RegistryObject<RefinementSet> ARMOR2_N_HEALTH2 =
            REFINEMENT_SETS.register("armor2_n_health2", () -> uncommonV(ModRefinements.ARMOR2.get(), ModRefinements.N_HEALTH2.get()));
    public static final RegistryObject<RefinementSet> HEALTH2_N_DAMAGE1 =
            REFINEMENT_SETS.register("health2_n_damage1", () -> uncommonV(ModRefinements.HEALTH2.get(), ModRefinements.N_DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> ATTACK_SPEED2_N_ARMOR1 =
            REFINEMENT_SETS.register("attack_speed2_n_armor1", () -> uncommonV(ModRefinements.ATTACK_SPEED2.get(), ModRefinements.N_ARMOR1.get()));
    public static final RegistryObject<RefinementSet> DAMAGE2_N_SPEED1 =
            REFINEMENT_SETS.register("damage2_n_speed1", () -> uncommonV(ModRefinements.DAMAGE2.get(), ModRefinements.N_SPEED1.get()));
    public static final RegistryObject<RefinementSet> SPEED2_N_DAMAGE1 =
            REFINEMENT_SETS.register("speed2_n_damage1", () -> uncommonV(ModRefinements.SPEED2.get(), ModRefinements.N_DAMAGE1.get()));

    // better attribute modifier with de-buffs
    public static final RegistryObject<RefinementSet> ARMOR3_N_HEALTH3 =
            REFINEMENT_SETS.register("armor3_n_health3", () -> uncommonV(ModRefinements.ARMOR3.get(), ModRefinements.N_HEALTH3.get()));
    public static final RegistryObject<RefinementSet> HEALTH3_N_DAMAGE2 =
            REFINEMENT_SETS.register("health3_n_damage2", () -> uncommonV(ModRefinements.HEALTH3.get(), ModRefinements.N_DAMAGE2.get()));
    public static final RegistryObject<RefinementSet> ATTACK_SPEED3_N_ARMOR2 =
            REFINEMENT_SETS.register("attack_speed3_n_armor2", () -> uncommonV(ModRefinements.ATTACK_SPEED3.get(), ModRefinements.N_ARMOR2.get()));
    public static final RegistryObject<RefinementSet> DAMAGE3_N_SPEED2 =
            REFINEMENT_SETS.register("damage3_n_speed2", () -> uncommonV(ModRefinements.DAMAGE3.get(), ModRefinements.N_SPEED3.get()));
    public static final RegistryObject<RefinementSet> SPEED3_N_DAMAGE2 =
            REFINEMENT_SETS.register("speed3_n_damage2", () -> uncommonV(ModRefinements.SPEED3.get(), ModRefinements.N_DAMAGE2.get()));

    //default skill upgrades
    public static final RegistryObject<RefinementSet> VISTA =
            REFINEMENT_SETS.register("vista", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.VISTA.get()));
    public static final RegistryObject<RefinementSet> TELEPORT_DISTANCE =
            REFINEMENT_SETS.register("teleport_distance", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.TELEPORT_DISTANCE.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE =
            REFINEMENT_SETS.register("dark_blood_projectile_damage", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION =
            REFINEMENT_SETS.register("dark_blood_projectile_penetration", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT =
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));

    //buffed skill upgrades
    public static final RegistryObject<RefinementSet> VISTA_BUFFED =
            REFINEMENT_SETS.register("vista_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.VISTA.get(), ModRefinements.SPEED1.get()));
    public static final RegistryObject<RefinementSet> TELEPORT_DISTANCE_BUFFED =
            REFINEMENT_SETS.register("teleport_distance_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.TELEPORT_DISTANCE.get(), ModRefinements.SPEED1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_damage_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE.get(), ModRefinements.DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_penetration_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION.get(), ModRefinements.ATTACK_SPEED1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT.get(), ModRefinements.ATTACK_SPEED1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));

    //de-buffed skill upgrades
    public static final RegistryObject<RefinementSet> VISTA_DEBUFFED =
            REFINEMENT_SETS.register("vista_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x0bafed, ModRefinements.VISTA.get(), ModRefinements.N_SPEED1.get()));
    public static final RegistryObject<RefinementSet> TELEPORT_DISTANCE_DEBUFFED =
            REFINEMENT_SETS.register("teleport_distance_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x068755, ModRefinements.TELEPORT_DISTANCE.get(), ModRefinements.N_SPEED1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_DAMAGE_DEBUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_damage_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE.get(), ModRefinements.N_DAMAGE1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_penetration_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION.get(), ModRefinements.N_ATTACK_SPEED1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_multi_shot_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT.get(), ModRefinements.N_ATTACK_SPEED1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));


    // Rare refinement sets
    // attribute modifier
    public static final RegistryObject<RefinementSet> ARMOR3_N_HEALTH2 =
            REFINEMENT_SETS.register("armor3_n_health2", () -> rareV(ModRefinements.ARMOR3.get(), ModRefinements.N_HEALTH2.get()));
    public static final RegistryObject<RefinementSet> HEALTH3_N_ARMOR1 =
            REFINEMENT_SETS.register("health3_n_armor1", () -> rareV(ModRefinements.HEALTH3.get(), ModRefinements.N_ARMOR1.get()));
    public static final RegistryObject<RefinementSet> ATTACK_SPEED3_N_SPEED1 =
            REFINEMENT_SETS.register("attack_speed3_n_speed1", () -> rareV(ModRefinements.ATTACK_SPEED3.get(), ModRefinements.N_SPEED1.get()));
    public static final RegistryObject<RefinementSet> SPEED1_ARMOR1_HEALTH1 =
            REFINEMENT_SETS.register("speed1_armor1_health1", () -> rareV(ModRefinements.SPEED1.get(), ModRefinements.ARMOR1.get(), ModRefinements.HEALTH1.get()));
    public static final RegistryObject<RefinementSet> DAMAGE3_N_ARMOR1 =
            REFINEMENT_SETS.register("damage3_n_armor1", () -> rareV(ModRefinements.DAMAGE3.get(), ModRefinements.N_ARMOR2.get()));
    public static final RegistryObject<RefinementSet> SPEED3_N_ATTACK_SPEED1 =
            REFINEMENT_SETS.register("speed3_n_attack_speed1", () -> rareV(ModRefinements.SPEED3.get(), ModRefinements.N_ATTACK_SPEED1.get()));
    public static final RegistryObject<RefinementSet> DAMAGE1_ATTACK_SPEED1 =
            REFINEMENT_SETS.register("damage1_attack_speed1", () -> rareV(ModRefinements.DAMAGE1.get(), ModRefinements.ATTACK_SPEED1.get()));

    // default skill upgrades
    public static final RegistryObject<RefinementSet> HALF_INVULNERABLE =
            REFINEMENT_SETS.register("half_invulnerable", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.HALF_INVULNERABLE.get()));
    public static final RegistryObject<RefinementSet> SUMMON_BATS =
            REFINEMENT_SETS.register("summon_bats", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.SUMMON_BATS.get()));
    public static final RegistryObject<RefinementSet> SUN_SCREEN =
            REFINEMENT_SETS.register("sun_screen", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.SUN_SCREEN.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_SPEED =
            REFINEMENT_SETS.register("dark_blood_projectile_speed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xeeabcd, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED.get()));

    // de-buffed skill upgrades
    public static final RegistryObject<RefinementSet> HALF_INVULNERABLE_DEBUFFED =
            REFINEMENT_SETS.register("half_invulnerable_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xa96db7, ModRefinements.HALF_INVULNERABLE.get(), ModRefinements.N_ARMOR1.get()));
    public static final RegistryObject<RefinementSet> SUMMON_BATS_DEBUFFED =
            REFINEMENT_SETS.register("summon_bats_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0x8b8a91, ModRefinements.SUMMON_BATS.get(), ModRefinements.N_HEALTH1.get()));
    public static final RegistryObject<RefinementSet> SUN_SCREEN_DEBUFFED =
            REFINEMENT_SETS.register("sun_screen_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.RARE, 0xedc20b, ModRefinements.SUN_SCREEN.get(), ModRefinements.N_ARMOR1.get()));

    //other
    public static final RegistryObject<RefinementSet> CRUCIFIX_RESISTANT =
            REFINEMENT_SETS.register("crucifix_resistant", () -> new RefinementSet.VampireRefinementSet(Rarity.UNCOMMON, 0x7DC2D1, ModRefinements.CRUCIFIX_RESISTANT.get()));

    // Epic refinement sets

    // combined skill upgrades
    public static final RegistryObject<RefinementSet> VAMPIRE_SWORD =
            REFINEMENT_SETS.register("vampire_sword", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xed3b0b, ModRefinements.SWORD_TRAINED_AMOUNT.get(), ModRefinements.BLOOD_CHARGE_SPEED.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE =
            REFINEMENT_SETS.register("dark_blood_projectile", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION.get(), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));

    // skill upgrades
    public static final RegistryObject<RefinementSet> RAGE_FURY =
            REFINEMENT_SETS.register("rage_fury", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xc10202, ModRefinements.RAGE_FURY.get()));
    public static final RegistryObject<RefinementSet> SUMMON_BATS_BUFFED =
            REFINEMENT_SETS.register("summon_bats_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0x8b8a91, ModRefinements.SUMMON_BATS.get(), ModRefinements.HEALTH1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_AOE =
            REFINEMENT_SETS.register("dark_blood_projectile_aoe", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> HALF_INVULNERABLE_BUFFED =
            REFINEMENT_SETS.register("half_invulnerable_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xa96db7, ModRefinements.HALF_INVULNERABLE.get(), ModRefinements.ARMOR1.get()));
    public static final RegistryObject<RefinementSet> SUN_SCREEN_BUFFED =
            REFINEMENT_SETS.register("sun_screen_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xedc20b, ModRefinements.SUN_SCREEN.get(), ModRefinements.ARMOR1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_SPEED_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_speed_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xeeab6d, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED.get(), ModRefinements.SPEED1.get()));

    // de-buffed skill upgrades
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_AOE_DEBUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_aoe_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE.get(), ModRefinements.N_HEALTH1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> RAGE_FURY_DEBUFFED =
            REFINEMENT_SETS.register("rage_fury_debuffed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0xc10202, ModRefinements.RAGE_FURY.get(), ModRefinements.N_ARMOR1.get()));


    //other
    public static final RegistryObject<RefinementSet> CRUCIFIX_RESISTANT_SPEED =
            REFINEMENT_SETS.register("crucifix_resistant_speed", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0x7D55D1, ModRefinements.CRUCIFIX_RESISTANT.get(), ModRefinements.ATTACK_SPEED1.get()));
    public static final RegistryObject<RefinementSet> CRUCIFIX_RESISTANT_HEALTH =
            REFINEMENT_SETS.register("crucifix_resistant_health", () -> new RefinementSet.VampireRefinementSet(Rarity.EPIC, 0x7D55D1, ModRefinements.CRUCIFIX_RESISTANT.get(), ModRefinements.HEALTH2.get()));

    // Legendary refinement sets
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_AOE_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_aoe_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xe303ff, ModRefinements.DARK_BLOOD_PROJECTILE_AOE.get(), ModRefinements.HEALTH1.get()).onlyFor(IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementSet> RAGE_FURY_BUFFED =
            REFINEMENT_SETS.register("rage_fury_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xc10202, ModRefinements.RAGE_FURY.get(), ModRefinements.ARMOR1.get()));
    public static final RegistryObject<RefinementSet> DARK_BLOOD_PROJECTILE_SPEED_DOUBLE_BUFFED =
            REFINEMENT_SETS.register("dark_blood_projectile_speed_double_buffed", () -> new RefinementSet.VampireRefinementSet(Rarity.LEGENDARY, 0xee5acd, ModRefinements.DARK_BLOOD_PROJECTILE_SPEED.get(), ModRefinements.SPEED3.get()));

    public static void registerRefinementSets(IEventBus bus) {
        REFINEMENT_SETS.register(bus);
    }

    private static RefinementSet commonV(IRefinement... refinements) {
        return vampire(Rarity.COMMON, refinements);
    }

    private static RefinementSet uncommonV(IRefinement... refinements) {
        return vampire(Rarity.UNCOMMON, refinements);
    }

    private static RefinementSet rareV(IRefinement... refinements) {
        return vampire(Rarity.RARE, refinements);
    }

    private static RefinementSet epicV(IRefinement... refinements) {
        return vampire(Rarity.EPIC, refinements);
    }

    @SuppressWarnings("ConstantConditions")
    private static RefinementSet vampire(Rarity rarity, IRefinement... refinements) {
        return new RefinementSet.VampireRefinementSet(rarity, rarity.color.getColor(), refinements);
    }
}
