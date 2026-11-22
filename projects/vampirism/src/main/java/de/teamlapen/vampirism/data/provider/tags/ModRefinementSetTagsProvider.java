package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.tags.FactionRefinementSetTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModRefinementSets;
import de.teamlapen.vampirism.common.tags.ModRefinementSetTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRefinementSetTagsProvider extends KeyTagProvider<IRefinementSet> {

    public ModRefinementSetTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, FactionRegistries.Keys.REFINEMENT_SET, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModRefinementSetTags.IS_VAMPIRE).add(
                ModRefinementSets.ARMOR1,
                ModRefinementSets.HEALTH1,
                ModRefinementSets.SPEED1,
                ModRefinementSets.ATTACK_SPEED1,
                ModRefinementSets.DAMAGE1,
                ModRefinementSets.DAMAGE1_ATTACK_SPEED1_N_ARMOR2,
                ModRefinementSets.ARMOR1_HEALTH1_N_ATTACK_SPEED2,
                ModRefinementSets.REGENERATION,
                ModRefinementSets.SWORD_TRAINED_AMOUNT,
                ModRefinementSets.BLOOD_CHARGE_SPEED,
                ModRefinementSets.FREEZE_DURATION,
                ModRefinementSets.SWORD_FINISHER,
                ModRefinementSets.REGENERATION_BUFFED,
                ModRefinementSets.SWORD_TRAINED_AMOUNT_BUFFED,
                ModRefinementSets.BLOOD_CHARGE_SPEED_BUFFED,
                ModRefinementSets.FREEZE_DURATION_BUFFED,
                ModRefinementSets.SWORD_FINISHER_BUFFED,
                ModRefinementSets.REGENERATION_DEBUFFED,
                ModRefinementSets.SWORD_TRAINED_AMOUNT_DEBUFFED,
                ModRefinementSets.BLOOD_CHARGE_SPEED_DEBUFFED,
                ModRefinementSets.FREEZE_DURATION_DEBUFFED,
                ModRefinementSets.SWORD_FINISHER_DEBUFFED,
                ModRefinementSets.ARMOR2_N_HEALTH2,
                ModRefinementSets.HEALTH2_N_DAMAGE1,
                ModRefinementSets.ATTACK_SPEED2_N_ARMOR1,
                ModRefinementSets.DAMAGE2_N_SPEED1,
                ModRefinementSets.SPEED2_N_DAMAGE1,
                ModRefinementSets.ARMOR3_N_HEALTH3,
                ModRefinementSets.HEALTH3_N_DAMAGE2,
                ModRefinementSets.ATTACK_SPEED3_N_ARMOR2,
                ModRefinementSets.DAMAGE3_N_SPEED2,
                ModRefinementSets.SPEED3_N_DAMAGE2,
                ModRefinementSets.VISTA,
                ModRefinementSets.TELEPORT_DISTANCE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT,
                ModRefinementSets.VISTA_BUFFED,
                ModRefinementSets.TELEPORT_DISTANCE_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED,
                ModRefinementSets.VISTA_DEBUFFED,
                ModRefinementSets.TELEPORT_DISTANCE_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED,
                ModRefinementSets.ARMOR3_N_HEALTH2,
                ModRefinementSets.HEALTH3_N_ARMOR1,
                ModRefinementSets.ATTACK_SPEED3_N_SPEED1,
                ModRefinementSets.SPEED1_ARMOR1_HEALTH1,
                ModRefinementSets.DAMAGE3_N_ARMOR1,
                ModRefinementSets.SPEED3_N_ATTACK_SPEED1,
                ModRefinementSets.DAMAGE1_ATTACK_SPEED1,
                ModRefinementSets.HALF_INVULNERABLE,
                ModRefinementSets.SUMMON_BATS,
                ModRefinementSets.SUN_SCREEN,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED,
                ModRefinementSets.HALF_INVULNERABLE_DEBUFFED,
                ModRefinementSets.SUMMON_BATS_DEBUFFED,
                ModRefinementSets.SUN_SCREEN_DEBUFFED,
                ModRefinementSets.CRUCIFIX_RESISTANT,
                ModRefinementSets.VAMPIRE_SWORD,
                ModRefinementSets.DARK_BLOOD_PROJECTILE,
                ModRefinementSets.RAGE_FURY,
                ModRefinementSets.SUMMON_BATS_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE,
                ModRefinementSets.HALF_INVULNERABLE_BUFFED,
                ModRefinementSets.SUN_SCREEN_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_DEBUFFED,
                ModRefinementSets.RAGE_FURY_DEBUFFED,
                ModRefinementSets.CRUCIFIX_RESISTANT_SPEED,
                ModRefinementSets.CRUCIFIX_RESISTANT_HEALTH,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_BUFFED,
                ModRefinementSets.RAGE_FURY_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED_DOUBLE_BUFFED
        );
        this.tag(FactionRefinementSetTags.COMMON).add(
                ModRefinementSets.ARMOR1,
                ModRefinementSets.HEALTH1,
                ModRefinementSets.SPEED1,
                ModRefinementSets.ATTACK_SPEED1,
                ModRefinementSets.DAMAGE1,
                ModRefinementSets.DAMAGE1_ATTACK_SPEED1_N_ARMOR2,
                ModRefinementSets.ARMOR1_HEALTH1_N_ATTACK_SPEED2,
                ModRefinementSets.REGENERATION,
                ModRefinementSets.SWORD_TRAINED_AMOUNT,
                ModRefinementSets.BLOOD_CHARGE_SPEED,
                ModRefinementSets.FREEZE_DURATION,
                ModRefinementSets.SWORD_FINISHER,
                ModRefinementSets.REGENERATION_BUFFED,
                ModRefinementSets.SWORD_TRAINED_AMOUNT_BUFFED,
                ModRefinementSets.BLOOD_CHARGE_SPEED_BUFFED,
                ModRefinementSets.FREEZE_DURATION_BUFFED,
                ModRefinementSets.SWORD_FINISHER_BUFFED,
                ModRefinementSets.REGENERATION_DEBUFFED,
                ModRefinementSets.SWORD_TRAINED_AMOUNT_DEBUFFED,
                ModRefinementSets.BLOOD_CHARGE_SPEED_DEBUFFED,
                ModRefinementSets.FREEZE_DURATION_DEBUFFED,
                ModRefinementSets.SWORD_FINISHER_DEBUFFED
        );
        this.tag(FactionRefinementSetTags.UNCOMMON).add(
                ModRefinementSets.ARMOR2_N_HEALTH2,
                ModRefinementSets.HEALTH2_N_DAMAGE1,
                ModRefinementSets.ATTACK_SPEED2_N_ARMOR1,
                ModRefinementSets.DAMAGE2_N_SPEED1,
                ModRefinementSets.SPEED2_N_DAMAGE1,
                ModRefinementSets.ARMOR3_N_HEALTH3,
                ModRefinementSets.HEALTH3_N_DAMAGE2,
                ModRefinementSets.ATTACK_SPEED3_N_ARMOR2,
                ModRefinementSets.DAMAGE3_N_SPEED2,
                ModRefinementSets.SPEED3_N_DAMAGE2,
                ModRefinementSets.VISTA,
                ModRefinementSets.TELEPORT_DISTANCE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT,
                ModRefinementSets.VISTA_BUFFED,
                ModRefinementSets.TELEPORT_DISTANCE_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED,
                ModRefinementSets.VISTA_DEBUFFED,
                ModRefinementSets.TELEPORT_DISTANCE_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_DAMAGE_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED,
                ModRefinementSets.CRUCIFIX_RESISTANT
        );
        this.tag(FactionRefinementSetTags.RARE).add(
                ModRefinementSets.ARMOR3_N_HEALTH2,
                ModRefinementSets.HEALTH3_N_ARMOR1,
                ModRefinementSets.ATTACK_SPEED3_N_SPEED1,
                ModRefinementSets.SPEED1_ARMOR1_HEALTH1,
                ModRefinementSets.DAMAGE3_N_ARMOR1,
                ModRefinementSets.SPEED3_N_ATTACK_SPEED1,
                ModRefinementSets.DAMAGE1_ATTACK_SPEED1,
                ModRefinementSets.HALF_INVULNERABLE,
                ModRefinementSets.SUMMON_BATS,
                ModRefinementSets.SUN_SCREEN,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED,
                ModRefinementSets.HALF_INVULNERABLE_DEBUFFED,
                ModRefinementSets.SUMMON_BATS_DEBUFFED,
                ModRefinementSets.SUN_SCREEN_DEBUFFED
        );
        this.tag(FactionRefinementSetTags.EPIC).add(
                ModRefinementSets.VAMPIRE_SWORD,
                ModRefinementSets.DARK_BLOOD_PROJECTILE,
                ModRefinementSets.RAGE_FURY,
                ModRefinementSets.SUMMON_BATS_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE,
                ModRefinementSets.HALF_INVULNERABLE_BUFFED,
                ModRefinementSets.SUN_SCREEN_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_DEBUFFED,
                ModRefinementSets.RAGE_FURY_DEBUFFED,
                ModRefinementSets.CRUCIFIX_RESISTANT_SPEED,
                ModRefinementSets.CRUCIFIX_RESISTANT_HEALTH
        );
        this.tag(FactionRefinementSetTags.LEGENDARY).add(
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_BUFFED,
                ModRefinementSets.RAGE_FURY_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_SPEED_DOUBLE_BUFFED
        );
        this.tag(FactionRefinementSetTags.RING).add(
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_BUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_PENETRATION_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_MULTI_SHOT_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_DEBUFFED,
                ModRefinementSets.DARK_BLOOD_PROJECTILE_AOE_BUFFED
        );
    }
}
