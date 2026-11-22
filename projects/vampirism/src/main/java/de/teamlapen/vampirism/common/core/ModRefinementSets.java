package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.refinements.Rarity;
import de.teamlapen.faction.api.world.items.AccessorySlotType;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.faction.common.core.FactionRefinements;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModRefinementSetTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Definitions of all vampirism refinement sets. Used by datagen to create the refinement set registry entries and their faction, rarity and slot tags.
 */
public class ModRefinementSets {

    @ApiStatus.Internal
    public static final List<Definition> SETS;

    static {
        List<Definition> sets = new ArrayList<>();
        Registrar register = (name, builder, refinements) -> sets.add(builder.build(name, refinements));
        createDefinitions(register);
        SETS = Collections.unmodifiableList(sets);
    }

    private static void createDefinitions(Registrar r) {
        // Common refinement set
        {
            // attribute modifier
            r.register("armor1", vampire(Rarity.COMMON), FactionRefinements.ARMOR1);
            r.register("health1", vampire(Rarity.COMMON), FactionRefinements.HEALTH1);
            r.register("speed1", vampire(Rarity.COMMON), FactionRefinements.SPEED1);
            r.register("attack_speed1", vampire(Rarity.COMMON), FactionRefinements.ATTACK_SPEED1);
            r.register("damage1", vampire(Rarity.COMMON), FactionRefinements.DAMAGE1);
            r.register("damage1_attack_speed1_n_armor2", vampire(Rarity.COMMON), FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1, FactionRefinements.N_ARMOR2);
            r.register("armor1_health1_n_attack_speed2", vampire(Rarity.COMMON), FactionRefinements.ARMOR1, FactionRefinements.HEALTH1, FactionRefinements.N_ATTACK_SPEED2);

            //default skill upgrades
            r.register("regeneration", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION);
            r.register("sword_trained_amount", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT);
            r.register("blood_charge_speed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED);
            r.register("freeze_duration", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION);
            r.register("sword_finisher", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER);

            //buffed skill upgrades
            r.register("regeneration_buffed", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION, FactionRefinements.SPEED1);
            r.register("sword_trained_amount_buffed", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.DAMAGE1);
            r.register("blood_charge_speed_buffed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.ATTACK_SPEED1);
            r.register("freeze_duration_buffed", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION, FactionRefinements.HEALTH1);
            r.register("sword_finisher_buffed", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER, FactionRefinements.ARMOR1);

            //de-buffed skill upgrades
            r.register("regeneration_debuffed", vampire(Rarity.COMMON, 0xedbf0b), ModRefinements.REGENERATION, FactionRefinements.N_SPEED1);
            r.register("sword_trained_amount_debuffed", vampire(Rarity.COMMON, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, FactionRefinements.N_DAMAGE1);
            r.register("blood_charge_speed_debuffed", vampire(Rarity.COMMON, 0xed250b), ModRefinements.BLOOD_CHARGE_SPEED, FactionRefinements.N_ATTACK_SPEED1);
            r.register("freeze_duration_debuffed", vampire(Rarity.COMMON, 0x0bdfed), ModRefinements.FREEZE_DURATION, FactionRefinements.N_HEALTH1);
            r.register("sword_finisher_debuffed", vampire(Rarity.COMMON, 0xd10106), ModRefinements.SWORD_FINISHER, FactionRefinements.N_ARMOR1);
        }

        //Uncommon refinement sets
        {
            // attribute modifier
            r.register("armor2_n_health2", vampire(Rarity.UNCOMMON), FactionRefinements.ARMOR2, FactionRefinements.N_HEALTH2);
            r.register("health2_n_damage1", vampire(Rarity.UNCOMMON), FactionRefinements.HEALTH2, FactionRefinements.N_DAMAGE1);
            r.register("attack_speed2_n_armor1", vampire(Rarity.UNCOMMON), FactionRefinements.ATTACK_SPEED2, FactionRefinements.N_ARMOR1);
            r.register("damage2_n_speed1", vampire(Rarity.UNCOMMON), FactionRefinements.DAMAGE2, FactionRefinements.N_SPEED1);
            r.register("speed2_n_damage1", vampire(Rarity.UNCOMMON), FactionRefinements.SPEED2, FactionRefinements.N_DAMAGE1);

            // better attribute modifier with de-buffs
            r.register("armor3_n_health3", vampire(Rarity.UNCOMMON), FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH3);
            r.register("health3_n_damage2", vampire(Rarity.UNCOMMON), FactionRefinements.HEALTH3, FactionRefinements.N_DAMAGE2);
            r.register("attack_speed3_n_armor2", vampire(Rarity.UNCOMMON), FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_ARMOR2);
            r.register("damage3_n_speed2", vampire(Rarity.UNCOMMON), FactionRefinements.DAMAGE3, FactionRefinements.N_SPEED3);
            r.register("speed3_n_damage2", vampire(Rarity.UNCOMMON), FactionRefinements.SPEED3, FactionRefinements.N_DAMAGE2);

            //default skill upgrades
            r.register("vista", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA);
            r.register("teleport_distance", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE);
            r.register("dark_blood_projectile_damage", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE);
            r.register("dark_blood_projectile_penetration", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION);
            r.register("dark_blood_projectile_multi_shot", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            //buffed skill upgrades
            r.register("vista_buffed", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA, FactionRefinements.SPEED1);
            r.register("teleport_distance_buffed", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE, FactionRefinements.SPEED1);
            r.register("dark_blood_projectile_damage_buffed", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.DAMAGE1);
            r.register("dark_blood_projectile_penetration_buffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.ATTACK_SPEED1);
            r.register("dark_blood_projectile_multi_shot_buffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.ATTACK_SPEED1);

            //de-buffed skill upgrades
            r.register("vista_debuffed", vampire(Rarity.UNCOMMON, 0x0bafed), ModRefinements.VISTA, FactionRefinements.N_SPEED1);
            r.register("teleport_distance_debuffed", vampire(Rarity.UNCOMMON, 0x068755), ModRefinements.TELEPORT_DISTANCE, FactionRefinements.N_SPEED1);
            r.register("dark_blood_projectile_damage_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff), ModRefinements.DARK_BLOOD_PROJECTILE_DAMAGE, FactionRefinements.N_DAMAGE1);
            r.register("dark_blood_projectile_penetration_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, FactionRefinements.N_ATTACK_SPEED1);
            r.register("dark_blood_projectile_multi_shot_debuffed", vampire(Rarity.UNCOMMON, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT, FactionRefinements.N_ATTACK_SPEED1);
        }

        // Rare refinement sets
        {
            // attribute modifier
            r.register("armor3_n_health2", vampire(Rarity.RARE), FactionRefinements.ARMOR3, FactionRefinements.N_HEALTH2);
            r.register("health3_n_armor1", vampire(Rarity.RARE), FactionRefinements.HEALTH3, FactionRefinements.N_ARMOR1);
            r.register("attack_speed3_n_speed1", vampire(Rarity.RARE), FactionRefinements.ATTACK_SPEED3, FactionRefinements.N_SPEED1);
            r.register("speed1_armor1_health1", vampire(Rarity.RARE), FactionRefinements.SPEED1, FactionRefinements.ARMOR1, FactionRefinements.HEALTH1);
            r.register("damage3_n_armor1", vampire(Rarity.RARE), FactionRefinements.DAMAGE3, FactionRefinements.N_ARMOR2);
            r.register("speed3_n_attack_speed1", vampire(Rarity.RARE), FactionRefinements.SPEED3, FactionRefinements.N_ATTACK_SPEED1);
            r.register("damage1_attack_speed1", vampire(Rarity.RARE), FactionRefinements.DAMAGE1, FactionRefinements.ATTACK_SPEED1);

            // default skill upgrades
            r.register("half_invulnerable", vampire(Rarity.RARE, 0xa96db7), ModRefinements.HALF_INVULNERABLE);
            r.register("summon_bats", vampire(Rarity.RARE, 0x8b8a91), ModRefinements.SUMMON_BATS);
            r.register("sun_screen", vampire(Rarity.RARE, 0xedc20b), ModRefinements.SUN_SCREEN);
            r.register("dark_blood_projectile_speed", vampire(Rarity.RARE, 0xeeabcd), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED);

            // de-buffed skill upgrades
            r.register("half_invulnerable_debuffed", vampire(Rarity.RARE, 0xa96db7), ModRefinements.HALF_INVULNERABLE, FactionRefinements.N_ARMOR1);
            r.register("summon_bats_debuffed", vampire(Rarity.RARE, 0x8b8a91), ModRefinements.SUMMON_BATS, FactionRefinements.N_HEALTH1);
            r.register("sun_screen_debuffed", vampire(Rarity.RARE, 0xedc20b), ModRefinements.SUN_SCREEN, FactionRefinements.N_ARMOR1);

            //other
            r.register("crucifix_resistant", vampire(Rarity.UNCOMMON, 0x7DC2D1), ModRefinements.CRUCIFIX_RESISTANT);

        }

        // Epic refinement sets
        {

            // combined skill upgrades
            r.register("vampire_sword", vampire(Rarity.EPIC, 0xed3b0b), ModRefinements.SWORD_TRAINED_AMOUNT, ModRefinements.BLOOD_CHARGE_SPEED);
            r.register("dark_blood_projectile", vampire(Rarity.EPIC, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_PENETRATION, ModRefinements.DARK_BLOOD_PROJECTILE_MULTI_SHOT);

            // skill upgrades
            r.register("rage_fury", vampire(Rarity.EPIC, 0xc10202), ModRefinements.RAGE_FURY);
            r.register("summon_bats_buffed", vampire(Rarity.EPIC, 0x8b8a91), ModRefinements.SUMMON_BATS, FactionRefinements.HEALTH1);
            r.register("dark_blood_projectile_aoe", vampire(Rarity.EPIC, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE);
            r.register("half_invulnerable_buffed", vampire(Rarity.EPIC, 0xa96db7), ModRefinements.HALF_INVULNERABLE, FactionRefinements.ARMOR1);
            r.register("sun_screen_buffed", vampire(Rarity.EPIC, 0xedc20b), ModRefinements.SUN_SCREEN, FactionRefinements.ARMOR1);
            r.register("dark_blood_projectile_speed_buffed", vampire(Rarity.EPIC, 0xeeab6d), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED1);

            // de-buffed skill upgrades
            r.register("dark_blood_projectile_aoe_debuffed", vampire(Rarity.EPIC, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.N_HEALTH1);
            r.register("rage_fury_debuffed", vampire(Rarity.EPIC, 0xc10202), ModRefinements.RAGE_FURY, FactionRefinements.N_ARMOR1);

            //other
            r.register("crucifix_resistant_speed", vampire(Rarity.EPIC, 0x7D55D1), ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.ATTACK_SPEED1);
            r.register("crucifix_resistant_health", vampire(Rarity.EPIC, 0x7D55D1), ModRefinements.CRUCIFIX_RESISTANT, FactionRefinements.HEALTH2);
        }
        // Legendary refinement sets
        {
            r.register("dark_blood_projectile_aoe_buffed", vampire(Rarity.LEGENDARY, 0xe303ff).onlyFor(AccessorySlotType.RING), ModRefinements.DARK_BLOOD_PROJECTILE_AOE, FactionRefinements.HEALTH1);
            r.register("rage_fury_buffed", vampire(Rarity.LEGENDARY, 0xc10202), ModRefinements.RAGE_FURY, FactionRefinements.ARMOR1);
            r.register("dark_blood_projectile_speed_double_buffed", vampire(Rarity.LEGENDARY, 0xee5acd), ModRefinements.DARK_BLOOD_PROJECTILE_SPEED, FactionRefinements.SPEED3);
        }
    }

    static void createRefinementSets(BootstrapContext<IRefinementSet> context) {
        HolderGetter<IRefinement> lookup = context.lookup(FactionRegistries.Keys.REFINEMENT);
        for (Definition set : SETS) {
            context.register(set.key(), RefinementSet.of(set.key(), set.color(), set.refinements().stream().map(lookup::getOrThrow).toList()));
        }
    }

    private static Builder vampire(Rarity rarity) {
        //noinspection DataFlowIssue
        return vampire(rarity, rarity.color.getColor());
    }

    private static Builder vampire(Rarity rarity, int color) {
        return new Builder(ModRefinementSetTags.IS_VAMPIRE, rarity, color);
    }

    /**
     * @param faction     the faction specific refinement set tag the set is added to
     * @param rarity      the rarity tag the set is added to
     * @param slots       the slot tags the set is added to. Empty if the set can be applied to all slots
     * @param refinements the refinements of the set
     */
    public record Definition(ResourceKey<IRefinementSet> key, TagKey<IRefinementSet> faction, Rarity rarity, int color, Set<AccessorySlotType> slots, List<ResourceKey<IRefinement>> refinements) {
    }

    @FunctionalInterface
    private interface Registrar {
        @SuppressWarnings("unchecked")
        void register(String name, Builder builder, ResourceKey<IRefinement>... refinements);
    }

    private static class Builder {
        private final TagKey<IRefinementSet> faction;
        private final Rarity rarity;
        private final int color;
        private final Set<AccessorySlotType> slots = EnumSet.noneOf(AccessorySlotType.class);

        private Builder(TagKey<IRefinementSet> faction, Rarity rarity, int color) {
            this.faction = faction;
            this.rarity = rarity;
            this.color = color;
        }

        /**
         * Restrict the set to the given accessory slot. Can be called multiple times to allow multiple slots
         */
        public Builder onlyFor(AccessorySlotType slot) {
            this.slots.add(slot);
            return this;
        }

        private Definition build(String name, ResourceKey<IRefinement>[] refinements) {
            return new Definition(ResourceKey.create(FactionRegistries.Keys.REFINEMENT_SET, VIdentifier.mod(name)), this.faction, this.rarity, this.color, Set.copyOf(this.slots), List.of(refinements));
        }
    }
}
