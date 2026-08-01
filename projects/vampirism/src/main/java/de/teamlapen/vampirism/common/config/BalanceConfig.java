package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.util.UtilLib;
import net.minecraft.core.registries.Registries;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Values are null until after RegistryEvent<Block>.
 */
public class BalanceConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    // General
    public final ModConfigSpec.IntValue quarrelVampireKillerMaxHealth;
    public final ModConfigSpec.IntValue holyWaterSplashDamage;
    public final ModConfigSpec.DoubleValue holyWaterTierDamageInc;
    public final ModConfigSpec.IntValue holyWaterNauseaDuration;
    public final ModConfigSpec.IntValue holyWaterBlindnessDuration;
    public final ModConfigSpec.DoubleValue vampireSwordChargingFactor;
    public final ModConfigSpec.DoubleValue vampireSwordBloodUsageFactor;
    public final ModConfigSpec.IntValue dropOrchidFromLeavesChance;
    public final ModConfigSpec.BooleanValue golemAttackNonVillageFaction;
    public final ModConfigSpec.BooleanValue zombieIgnoreVampire;
    public final ModConfigSpec.BooleanValue skeletonIgnoreVampire;
    public final ModConfigSpec.BooleanValue creeperIgnoreVampire;
    public final ModConfigSpec.DoubleValue bleedingEffectDamage;
    public final ModConfigSpec.IntValue diffuserBootTime;
    public final ModConfigSpec.IntValue hunterTentMaxSpawn;
    public final ModConfigSpec.DoubleValue crossbowDamageMult;
    public final ModConfigSpec.IntValue garlicDiffuserStartupTime;

    // Hunter Actions
    public final ModConfigSpec.BooleanValue haDisguiseEnabled;
    public final ModConfigSpec.DoubleValue haDisguiseVisibilityMod;
    public final ModConfigSpec.IntValue haDisguiseInvisibleSQ;
    public final ModConfigSpec.BooleanValue haAwarenessEnabled;
    public final ModConfigSpec.IntValue haAwarenessDuration;
    public final ModConfigSpec.IntValue haAwarenessCooldown;
    public final ModConfigSpec.IntValue haAwarenessRadius;
    public final ModConfigSpec.BooleanValue haPotionResistanceEnabled;
    public final ModConfigSpec.IntValue haPotionResistanceDuration;
    public final ModConfigSpec.IntValue haPotionResistanceCooldown;

    // Hunter Player
    public final ModConfigSpec.DoubleValue hpStrengthMaxMod;
    public final ModConfigSpec.DoubleValue hpStrengthType;

    // Hunter Skills
    public final ModConfigSpec.DoubleValue hsSmallAttackSpeedModifier;
    public final ModConfigSpec.DoubleValue hsSmallAttackDamageModifier;
    public final ModConfigSpec.BooleanValue hsInstantKill1FromBehind;
    public final ModConfigSpec.BooleanValue hsInstantKill1Player;
    public final ModConfigSpec.DoubleValue hsInstantKill1MaxHealth;
    public final ModConfigSpec.IntValue hsInstantKill2MaxHealth;
    public final ModConfigSpec.BooleanValue hsInstantKill2OnlyNPC;
    public final ModConfigSpec.IntValue hsGarlicDiffuserNormalDist;
    public final ModConfigSpec.IntValue hsGarlicDiffuserEnhancedDist;
    public final ModConfigSpec.IntValue hsGarlicDiffuserWeakDist;

    // Vampire Skills
    public final ModConfigSpec.DoubleValue vsSundamageReduction1;
    public final ModConfigSpec.DoubleValue vsBloodThirstReduction1;
    public final ModConfigSpec.DoubleValue vsSwordFinisherMaxHealth;
    public final ModConfigSpec.BooleanValue vsSwordFinisherOnPlayer;
    public final ModConfigSpec.IntValue vsJumpBoost;
    public final ModConfigSpec.DoubleValue vsSpeedBoost;
    public final ModConfigSpec.IntValue vsBloodVisionDistanceSq;
    public final ModConfigSpec.DoubleValue vsSmallAttackDamageModifier;
    public final ModConfigSpec.DoubleValue vsSmallAttackDamageMultiplier;
    public final ModConfigSpec.DoubleValue vsSmallAttackSpeedModifier;
    public final ModConfigSpec.DoubleValue vsNeonatalReduction;
    public final ModConfigSpec.DoubleValue vsDbnoReduction;

    // Vampire Player
    public final ModConfigSpec.DoubleValue vpHealthMaxLevelMod;
    public final ModConfigSpec.DoubleValue vpAttackSpeedMaxLevelMod;
    public final ModConfigSpec.DoubleValue vpSpeedMaxLevelMod;
    public final ModConfigSpec.DoubleValue vpExhaustionMaxLevelMod;
    public final ModConfigSpec.DoubleValue vpBloodExhaustionFactor;
    public final ModConfigSpec.BooleanValue vpBloodUsagePeaceful;
    public final ModConfigSpec.DoubleValue vpPlayerBloodSaturation;
    public final ModConfigSpec.IntValue vpSanguinareAverageDuration;
    public final ModConfigSpec.IntValue vpSundamageMinLevel;
    public final ModConfigSpec.BooleanValue vpSundamageNausea;
    public final ModConfigSpec.IntValue vpSundamageNauseaMinLevel;
    public final ModConfigSpec.IntValue vpSundamageWeaknessMinLevel;
    public final ModConfigSpec.DoubleValue vpSundamagePerTick;
    public final ModConfigSpec.IntValue vpSundamageWaterblocks;
    public final ModConfigSpec.BooleanValue vpSundamageInstantDeath;
    public final ModConfigSpec.BooleanValue vpSunscreenBuff;
    public final ModConfigSpec.DoubleValue vpFireVulnerabilityMod;
    public final ModConfigSpec.BooleanValue vpFireResistanceReplace;
    public final ModConfigSpec.IntValue vpMaxYellowBorderPercentage;
    public final ModConfigSpec.ConfigValue<List<? extends String>> vpImmortalFromDamageSources;
    public final ModConfigSpec.IntValue vpDbnoDuration;
    public final ModConfigSpec.IntValue vpDbnoMinLevel;
    public final ModConfigSpec.IntValue vpNeonatalDuration;
    public final ModConfigSpec.IntValue vpNaturalArmorRegenDuration;
    public final ModConfigSpec.IntValue vpNaturalArmorBaseValue;
    public final ModConfigSpec.IntValue vpNaturalArmorIncrease;
    public final ModConfigSpec.IntValue vpNaturalArmorToughnessIncrease;
    public final ModConfigSpec.BooleanValue vpArmorPenalty;
    public final ModConfigSpec.BooleanValue vpNightVisionDisabled;
    public final ModConfigSpec.BooleanValue vpBloodVisionDisabled;

    // Vampire Actions
    public final ModConfigSpec.IntValue vaFreezeCooldown;
    public final ModConfigSpec.BooleanValue vaFreezeEnabled;
    public final ModConfigSpec.IntValue vaFreezeDuration;
    public final ModConfigSpec.DoubleValue vaFreezeAttackSpeedModifier;
    public final ModConfigSpec.IntValue vaInvisibilityDuration;
    public final ModConfigSpec.IntValue vaInvisibilityCooldown;
    public final ModConfigSpec.BooleanValue vaInvisibilityEnabled;
    public final ModConfigSpec.IntValue vaRegenerationCooldown;
    public final ModConfigSpec.IntValue vaRegenerationDuration;
    public final ModConfigSpec.BooleanValue vaRegenerationEnabled;
    public final ModConfigSpec.IntValue vaTeleportMaxDistance;
    public final ModConfigSpec.IntValue vaTeleportCooldown;
    public final ModConfigSpec.BooleanValue vaTeleportEnabled;
    public final ModConfigSpec.IntValue vaRageCooldown;
    public final ModConfigSpec.IntValue vaRageMinDuration;
    public final ModConfigSpec.IntValue vaRageDurationIncrease;
    public final ModConfigSpec.BooleanValue vaRageEnabled;
    public final ModConfigSpec.IntValue vaSunscreenCooldown;
    public final ModConfigSpec.IntValue vaSunscreenDuration;
    public final ModConfigSpec.BooleanValue vaSunscreenEnabled;
    public final ModConfigSpec.IntValue vaBatCooldown;
    public final ModConfigSpec.IntValue vaBatDuration;
    public final ModConfigSpec.BooleanValue vaBatEnabled;
    public final ModConfigSpec.DoubleValue vaBatHealthReduction;
    public final ModConfigSpec.DoubleValue vaBatExhaustion;
    public final ModConfigSpec.DoubleValue vaBatFlightSpeed;
    public final ModConfigSpec.BooleanValue vaBatAllowInteraction;
    public final ModConfigSpec.BooleanValue vaSummonBatsEnabled;
    public final ModConfigSpec.IntValue vaSummonBatsCooldown;
    public final ModConfigSpec.IntValue vaSummonBatsCount;
    public final ModConfigSpec.IntValue vaDisguiseDuration;
    public final ModConfigSpec.IntValue vaDisguiseCooldown;
    public final ModConfigSpec.BooleanValue vaDisguiseEnabled;
    public final ModConfigSpec.IntValue vaDarkBloodProjectileCooldown;
    public final ModConfigSpec.BooleanValue vaDarkBloodProjectileEnabled;
    public final ModConfigSpec.DoubleValue vaDarkBloodProjectileDamage;
    public final ModConfigSpec.IntValue vaHalfInvulnerableCooldown;
    public final ModConfigSpec.IntValue vaHalfInvulnerableDuration;
    public final ModConfigSpec.IntValue vaHalfInvulnerableBloodCost;
    public final ModConfigSpec.DoubleValue vaHalfInvulnerableThreshold;
    public final ModConfigSpec.BooleanValue vaHalfInvulnerableEnabled;
    public final ModConfigSpec.BooleanValue vaHissingEnabled;
    public final ModConfigSpec.IntValue vaHissingCooldown;
    public final ModConfigSpec.BooleanValue vaJumpBoostEnabled;
    public final ModConfigSpec.IntValue vaJumpBoostCooldown;
    public final ModConfigSpec.IntValue vaJumpBoostDuration;
    public final ModConfigSpec.BooleanValue vaDarkStalkerEnabled;
    public final ModConfigSpec.IntValue vaDarkStalkerCooldown;
    public final ModConfigSpec.IntValue vaDarkStalkerDuration;
    public final ModConfigSpec.DoubleValue vaDarkStalkerBloodConsumption;

    // Minions
    public final ModConfigSpec.IntValue miResourceCooldown;

    // Vampire Refinements
    public final ModConfigSpec.DoubleValue vrSwordTrainingSpeedMod;
    public final ModConfigSpec.IntValue vrBloodChargeSpeedMod;
    public final ModConfigSpec.DoubleValue vrFreezeDurationMod;
    public final ModConfigSpec.DoubleValue vrVistaMod;
    public final ModConfigSpec.DoubleValue vrDarkBloodProjectileDamageMod;
    public final ModConfigSpec.DoubleValue vrDarkBloodProjectileAOECooldownMod;
    public final ModConfigSpec.IntValue vrDarkBloodProjectileAOERange;
    public final ModConfigSpec.DoubleValue vrSunscreenDurationMod;
    public final ModConfigSpec.IntValue vrRageFuryDurationBonus;
    public final ModConfigSpec.DoubleValue vrTeleportDistanceMod;
    public final ModConfigSpec.DoubleValue vrTeleportCooldownMod;
    public final ModConfigSpec.DoubleValue vrHalfInvulnerableThresholdMod;
    public final ModConfigSpec.DoubleValue vrSwordFinisherThresholdMod;

    // Items
    public final ModConfigSpec.BooleanValue itApplicableOilArmorReverse;
    public final ModConfigSpec.BooleanValue itApplicableOilPickaxeReverse;
    public final ModConfigSpec.BooleanValue itApplicableOilSwordReverse;

    // Effects
    public final ModConfigSpec.DoubleValue efExposedPerLevelMultiplier;

    // Lord Actions
    public final ModConfigSpec.BooleanValue laLordSpeedEnabled;
    public final ModConfigSpec.IntValue laLordSpeedCooldown;
    public final ModConfigSpec.IntValue laLordSpeedDuration;
    public final ModConfigSpec.BooleanValue laLordAttackSpeedEnabled;
    public final ModConfigSpec.IntValue laLordAttackSpeedCooldown;
    public final ModConfigSpec.IntValue laLordAttackSpeedDuration;

    public BalanceConfig(BalanceBuilder builder) {
        boolean iceAndFire = ModList.get().isLoaded("iceandfire");
        if (iceAndFire) {
            LOGGER.info("IceAndFire is loaded -> Adjusting default fire related configuration.");
        }

        builder.comment("General options.");
        builder.category("general", "");

        quarrelVampireKillerMaxHealth = builder
                .comment("The vampire killer arrow can only instantly kill NPC vampires whose maximum health does not exceed this value.")
                .defineInRange("quarrelVampireKillerMaxHealth", 40, 1, Integer.MAX_VALUE);
        holyWaterSplashDamage = builder
                .comment("Damage dealt by a normal holy water splash bottle when it directly hits a vampire.")
                .defineInRange("holyWaterSplashDamage", 5, 0, Integer.MAX_VALUE);
        holyWaterTierDamageInc = builder
                .comment("Holy water damage is multiplied by this value for each tier above normal.")
                .defineInRange("holyWaterTierDamageInc", 2d, 1d, 10d);
        holyWaterNauseaDuration = builder
                .comment("Duration of the nausea effect caused by enhanced or special holy water, in ticks.")
                .defineInRange("holyWaterNauseaDuration", 200, 0, 1000);
        holyWaterBlindnessDuration = builder
                .comment("Duration of the blindness effect caused by special holy water, in ticks.")
                .defineInRange("holyWaterBlindnessDuration", 160, 0, 1000);
        vampireSwordChargingFactor = builder
                .comment("Amount of blood in mB required to charge one percent of the vampire sword.")
                .defineInRange("vampireSwordChargingFactor", 0.05 / (double) VReference.FOOD_TO_FLUID_BLOOD, 0d, 1d);
        vampireSwordBloodUsageFactor = builder
                .comment("Percentage of stored blood consumed per hit with a vampire sword.")
                .defineInRange("vampireSwordBloodUsageFactor", 0.5, 0, 100d);
        dropOrchidFromLeavesChance = builder
                .comment("Chance of dropping an orchid when breaking leaves in the vampire forest, expressed as 1-in-N.")
                .defineInRange("dropOrchidFromLeavesChance", 25, 1, Integer.MAX_VALUE);
        golemAttackNonVillageFaction = builder
                .comment("When enabled, iron golems will attack faction NPCs that belong to a different faction than the village they are in.")
                .define("golemAttackNonVillageFaction", true);
        zombieIgnoreVampire = builder
                .comment("When enabled, zombies will not target vampire players.")
                .define("zombieIgnoreVampire", true);
        skeletonIgnoreVampire = builder
                .comment("When enabled, skeletons will not target vampire players.")
                .define("skeletonIgnoreVampire", true);
        creeperIgnoreVampire = builder
                .comment("When enabled, creepers will not target vampire players.")
                .define("creeperIgnoreVampire", true);
        hunterTentMaxSpawn = builder
                .comment("Maximum number of hunters that can spawn at a single tent per day.")
                .defineInRange("hunterTentMaxSpawn", 4, 0, 20);
        crossbowDamageMult = builder
                .comment("Multiplier applied to base damage dealt by quarrels.")
                .defineInRange("crossbowDamageMult", 1, 0.2, 5);
        garlicDiffuserStartupTime = builder
                .comment("Delay in ticks before a newly placed garlic diffuser becomes active. Scaled to 0.25x in singleplayer.")
                .defineInRange("garlicDiffuserStartupTime", 5 * 20, 1, 10000);
        bleedingEffectDamage = builder
                .comment("Damage dealt by the bleeding effect per damaging tick.")
                .defineInRange("bleedingEffectDamage", 0.1, 0, Double.MAX_VALUE);
        diffuserBootTime = builder
                .comment("Time in seconds before a diffuser's effect becomes active after placement.")
                .defineInRange("diffuserBootTime", 15, 1, Integer.MAX_VALUE / 20);


        builder.category("hunterActions", "ha");

        haDisguiseEnabled = builder
                .comment("When enabled, hunters can use the disguise action.")
                .define("disguiseEnabled", true);
        haDisguiseVisibilityMod = builder
                .comment("Multiplier applied to mob detection radius while a hunter is disguised.")
                .defineInRange("disguiseVisibilityMod", 0.2D, 0, 1);
        haDisguiseInvisibleSQ = builder
                .comment("Squared distance within which a disguised hunter is completely invisible to mobs.")
                .defineInRange("disguiseInvisibleSQ", 256, 1, Integer.MAX_VALUE);
        haAwarenessEnabled = builder
                .comment("When enabled, hunters can use the vampire awareness action.")
                .define("awarenessEnabled", true);
        haAwarenessDuration = builder
                .comment("Duration of the vampire awareness action, in ticks.")
                .defineInRange("awarenessDuration", 300, 1, Integer.MAX_VALUE);
        haAwarenessCooldown = builder
                .comment("Cooldown for the vampire awareness action, in ticks.")
                .defineInRange("awarenessCooldown", 900, 1, Integer.MAX_VALUE);
        haAwarenessRadius = builder
                .comment("Radius in blocks within which vampires are detected by the awareness action.")
                .defineInRange("awarenessRadius", 25, 0, 50);
        haPotionResistanceEnabled = builder
                .comment("When enabled, hunters can use the potion resistance action.")
                .define("potionResistanceEnabled", true);
        haPotionResistanceDuration = builder
                .comment("Duration of the potion resistance action, in ticks.")
                .defineInRange("potionResistanceDuration", 400, 1, Integer.MAX_VALUE);
        haPotionResistanceCooldown = builder
                .comment("Cooldown for the potion resistance action, in ticks.")
                .defineInRange("potionResistanceCooldown", 1200, 1, Integer.MAX_VALUE);

        builder.category("hunterPlayer", "hp");

        hpStrengthMaxMod = builder
                .comment("Maximum strength modifier for hunters at max level. Final strength = base * (modifier + 1).")
                .defineInRange("strengthMaxMod", 0.3d, 0d, 4d);
        hpStrengthType = builder
                .comment("Scaling type for the hunter strength modifier. Use 0.5 for square root scaling or 1.0 for linear scaling.")
                .defineInRange("strengthType", 0.5d, 0.5d, 1);

        builder.category("hunterSkills", "hs");

        hsSmallAttackSpeedModifier = builder
                .comment("Attack speed modifier from the basic attack speed skill. Final cooldown = 1 / (base * (1 + modifier)).")
                .defineInRange("smallAttackSpeedModifier", 0.3, 0, 3);
        hsSmallAttackDamageModifier = builder
                .comment("Flat damage added to base attack damage by the basic damage skill.")
                .defineInRange("smallAttackDamageModifier", 0.1d, 0, 10);
        hsInstantKill1FromBehind = builder
                .comment("When enabled, the first stake skill requires attacking from behind to instantly kill low-level vampires.")
                .define("instantKill1FromBehind", false);
        hsInstantKill1Player = builder
                .comment("Allow killing players")
                .define("instantKill1Player", true);
        hsInstantKill1MaxHealth = builder
                .comment("Maximum relative health an entity may have to be instantly killed by the first stake skill.")
                .defineInRange("instantKill1MaxHealth", 0.35, 0, 1);
        hsInstantKill2MaxHealth = builder
                .comment("Maximum absolute health an entity may have to be instantly killed from behind by the second stake skill.")
                .defineInRange("instantKill2MaxHealth", 200, 0, Integer.MAX_VALUE);
        hsInstantKill2OnlyNPC = builder
                .comment("When enabled, the second stake skill can only instantly kill NPCs, not other players.")
                .define("instantKill2OnlyNPC", true);
        hsGarlicDiffuserNormalDist = builder
                .comment("Chunk radius affected by a normal garlic diffuser. A value of 0 results in a single-chunk area. Only affects newly placed blocks.")
                .defineInRange("garlicDiffuserNormalDist", 0, 0, 5);
        hsGarlicDiffuserEnhancedDist = builder
                .comment("Chunk radius affected by an enhanced garlic diffuser. A value of 0 results in a single-chunk area. Only affects newly placed blocks.")
                .defineInRange("garlicDiffuserEnhancedDist", 1, 0, 5);
        hsGarlicDiffuserWeakDist = builder
                .comment("Chunk radius affected by a weak garlic diffuser. A value of 0 results in a single-chunk area. Only affects newly placed blocks.")
                .defineInRange("garlicDiffuserWeakDist", 2, 0, 5);

        builder.category("vampireSkills", "vs");

        vsSundamageReduction1 = builder
                .comment("Sun damage multiplier reduction from the first sun damage skill. Final sun damage = base * (1 + value).")
                .defineInRange("sundamageReduction1", -0.5, -1, 0);
        vsBloodThirstReduction1 = builder
                .comment("Blood exhaustion multiplier reduction from the first blood thirst skill. Final exhaustion = base * (1 + value).")
                .defineInRange("bloodThirstReduction1", -0.4, -1, 0);
        vsSwordFinisherMaxHealth = builder
                .comment("Maximum relative health a target may have to be finished by the vampire sword finisher skill.")
                .defineInRange("swordFinisherMaxHealth", 0.25, 0, 1);
        vsSwordFinisherOnPlayer = builder
                .comment("If the sword finisher works on players")
                .define("swordFinisherOnPlayer", true);
        vsJumpBoost = builder
                .comment("Jump boost level granted by the vampire jump skill. Equivalent to the potion effect amplifier, where -1 is no boost.")
                .defineInRange("jumpBoost", 1, -1, 5);
        vsSpeedBoost = builder
                .comment("Maximum speed boost granted by the vampire speed skill. Final max speed = base * (1 + value).")
                .defineInRange("speedBoost", 0.15, 0, 3);
        vsBloodVisionDistanceSq = builder
                .comment("Squared distance within which entities are highlighted by the blood vision skill.")
                .defineInRange("bloodVisionDistanceSq", 1600, 5, Integer.MAX_VALUE);
        vsSmallAttackDamageModifier = builder
                .comment("Flat damage added to base attack damage by the vampire basic damage skill.")
                .defineInRange("smallAttackDamageModifier", 1d, 0, 10d);
        vsSmallAttackDamageMultiplier = builder
                .comment("Damage to multiply as total (value + 1)")
                .defineInRange("smallAttackDamageMultiplier", 0.1f,0,1);
        vsSmallAttackSpeedModifier = builder
                .comment("Attack speed modifier from the vampire basic attack speed skill. Final cooldown = 1 / (base * (1 + modifier)).")
                .defineInRange("smallAttackSpeedModifier", 0.15, 0, 3);
        vsNeonatalReduction = builder
                .comment("Percentage reduction of the neonatal debuff duration from the neonatal reduction skill.")
                .defineInRange("neonatalReduction", 0.5, 0, 1024);
        vsDbnoReduction = builder
                .comment("Percentage reduction of the downed timer required to resurrect, from the downed reduction skill.")
                .defineInRange("dbnoReduction", 0.5, 0, 1024);

        builder.category("vampirePlayer", "vp");

        vpHealthMaxLevelMod = builder
                .comment("Flat bonus health added to vampire players at max level. This is an additive modifier.")
                .defineInRange("healthMaxLevelMod", 16, 0.5, 40);
        vpAttackSpeedMaxLevelMod = builder
                .comment("Maximum attack speed modifier for vampires at max level. This is a multiplicative modifier applied as (1 + modifier).")
                .defineInRange("attackSpeedMaxLevelMod", 0.15, 0, 2);
        vpSpeedMaxLevelMod = builder
                .comment("Maximum movement speed modifier for vampires at max level. This is a multiplicative modifier applied as (1 + modifier).")
                .defineInRange("speedMaxLevelMod", 0.5, 0, 5);
        vpExhaustionMaxLevelMod = builder
                .comment("Maximum exhaustion reduction modifier for vampires at max level. This is a multiplicative modifier applied as (1 + modifier).")
                .defineInRange("exhaustionMaxLevelMod", 1.0, 0, 10);
        vpBloodExhaustionFactor = builder
                .comment("Multiplier applied to blood exhaustion for vampire players.")
                .defineInRange("bloodExhaustionFactor", 0.7, 0, 5);
        vpBloodUsagePeaceful = builder
                .comment("When enabled, blood is consumed from vampire players even in peaceful game mode.")
                .define("bloodUsagePeaceful", false);
        vpPlayerBloodSaturation = builder
                .comment("Blood saturation value granted when a vampire player feeds.")
                .defineInRange("playerBloodSaturation", 1.5, 0.3, 10);
        vpSanguinareAverageDuration = builder
                .comment("Average duration of the Sanguinare Vampiris effect, in seconds. The actual duration is randomized between 0.5x and 1.5x this value.")
                .defineInRange("sanguinareAverageDuration", 900, 1, 10000);
        vpSundamagePerTick = builder
                .comment("Base sun damage dealt to vampire players per tick in direct sunlight.")
                .defineInRange("sundamagePerTick", 7d, 1, Double.MAX_VALUE);
        vpSundamageMinLevel = builder
                .comment("Minimum vampire level required to receive sun damage.")
                .defineInRange("sundamageMinLevel", 4, 1, Integer.MAX_VALUE);
        vpSundamageNausea = builder
                .comment("When enabled, vampire players receiving sun damage will also receive a nausea effect.")
                .define("sundamageNausea", true);
        vpSundamageNauseaMinLevel = builder
                .comment("Minimum vampire level required to receive the sun damage nausea effect.")
                .defineInRange("sundamageNauseaMinLevel", 3, 1, Integer.MAX_VALUE);
        vpSundamageWeaknessMinLevel = builder
                .comment("Minimum vampire level required to receive the sun damage weakness effect.")
                .defineInRange("sundamageWeaknessMinLevel", 2, 1, Integer.MAX_VALUE);
        vpSundamageWaterblocks = builder
                .comment("Number of water source blocks above a vampire required to block sun damage.")
                .defineInRange("sundamageWaterblocks", 4, 1, 10);
        vpSundamageInstantDeath = builder
                .comment("When enabled, vampires are instantly turned to ash upon exposure to direct sunlight.")
                .define("sundamageInstantDeath", false);
        vpSunscreenBuff = builder
                .comment("When enabled, the sunscreen potion prevents all sun-related negative effects regardless of vampire level.")
                .define("sunscreenBuff", false);
        vpFireVulnerabilityMod = builder
                .comment("Multiplier applied to fire damage received by vampire players." + (iceAndFire ? " Changed due to IceAndFire." : ""))
                .defineInRange("fireVulnerabilityMod", iceAndFire ? 1.5d : 3d, 0.1, Double.MAX_VALUE);
        vpFireResistanceReplace = builder
                .comment("When enabled, replaces the vanilla fire resistance effect for vampires with a weaker version that reduces but does not eliminate fire damage." + (iceAndFire ? " Changed due to IceAndFire." : ""))
                .define("fireResistanceReplace", !iceAndFire);
        vpMaxYellowBorderPercentage = builder
                .comment("Maximum extent of the yellow vignette shown when a vampire player is in sunlight. Set to 0 to disable the vignette entirely.")
                .defineInRange("maxYellowBorderPercentage", 100, 0, 100);
        vpImmortalFromDamageSources = builder
                .comment("List of damage source types that will not immediately kill a vampire player. Use damage type IDs e.g. [\"minecraft:fell_out_of_world\"].")
                .defineList("immortalFromDamageSources", List.of(), () -> "", c -> UtilLib.checkRegistryObjectExistence(Registries.DAMAGE_TYPE, c));
        vpDbnoDuration = builder
                .comment("Base duration a downed vampire must wait before being able to resurrect, in seconds.")
                .defineInRange("dbnoDuration", 60, 1, 1000);
        vpDbnoMinLevel = builder
                .comment("Minimum vampire level required to go down instead of dying. Lower level vampires die normally.")
                .defineInRange("dbnoMinLevel", 4, 1, Integer.MAX_VALUE);
        vpNeonatalDuration = builder
                .comment("Base duration of the neonatal debuff applied after a vampire resurrects, in seconds.")
                .defineInRange("neonatalDuration", 120, 1, Integer.MAX_VALUE);
        vpNaturalArmorRegenDuration = builder
                .comment("Time for a vampire's natural armor to fully regenerate after respawning, in seconds.")
                .defineInRange("naturalArmorRegenDuration", 240, 1, 2400);
        vpNaturalArmorBaseValue = builder
                .comment("Base natural armor value granted to all vampires at level 1.")
                .defineInRange("naturalArmorBaseValue", 10, 0, 100);
        vpNaturalArmorIncrease = builder
                .comment("Additional natural armor granted to vampires at max level, on top of the base value.")
                .defineInRange("naturalArmorIncrease", 10, 0, 100);
        vpNaturalArmorToughnessIncrease = builder
                .comment("Natural armor toughness granted to vampires at max level.")
                .defineInRange("naturalArmorToughnessIncrease", 8, 0, 100);
        vpArmorPenalty = builder
                .comment("When enabled, vampires wearing heavy armor receive reduced speed and attack speed bonuses.")
                .define("armorPenalty", true);
        vpNightVisionDisabled = builder
                .comment("When enabled, disables the passive night vision effect for vampire players.")
                .define("nightVisionDisabled", false);
        vpBloodVisionDisabled = builder
                .comment("When enabled, disables the blood vision ability for vampire players.")
                .define("bloodVisionDisabled", false);

        builder.category("vampireActions", "va");

        vaFreezeEnabled = builder
                .comment("When enabled, vampires can use the freeze action.")
                .define("freezeEnabled", true);
        vaFreezeCooldown = builder
                .comment("Cooldown for the vampire freeze action, in seconds.")
                .defineInRange("freezeCooldown", 60, 1, Integer.MAX_VALUE);
        vaFreezeDuration = builder
                .comment("Duration of the vampire freeze action, in seconds.")
                .defineInRange("freezeDuration", 5, 1, 30);
        vaFreezeAttackSpeedModifier = builder
                .comment("Attack speed reduction applied to entities affected by the vampire freeze action.")
                .defineInRange("freezeAttackSpeedModifier", 0.3, 0, 1);
        vaInvisibilityEnabled = builder
                .comment("When enabled, vampires can use the invisibility action.")
                .define("invisibilityEnabled", true);
        vaInvisibilityCooldown = builder
                .comment("Cooldown for the vampire invisibility action, in seconds.")
                .defineInRange("invisibilityCooldown", 25, 1, Integer.MAX_VALUE);
        vaInvisibilityDuration = builder
                .comment("Duration of the vampire invisibility action, in seconds.")
                .defineInRange("invisibilityDuration", 25, 1, Integer.MAX_VALUE);
        vaRegenerationEnabled = builder
                .comment("When enabled, vampires can use the regeneration action.")
                .define("regenerationEnabled", true);
        vaRegenerationCooldown = builder
                .comment("Cooldown for the vampire regeneration action, in seconds.")
                .defineInRange("regenerationCooldown", 60, 0, Integer.MAX_VALUE);
        vaRegenerationDuration = builder
                .comment("Duration of the vampire regeneration action, in seconds.")
                .defineInRange("regenerationDuration", 20, 0, Integer.MAX_VALUE);
        vaTeleportEnabled = builder
                .comment("When enabled, vampires can use the teleport action.")
                .define("teleportEnabled", true);
        vaTeleportCooldown = builder
                .comment("Cooldown for the vampire teleport action, in seconds.")
                .defineInRange("teleportCooldown", 10, 1, Integer.MAX_VALUE);
        vaTeleportMaxDistance = builder
                .comment("Maximum distance in blocks a vampire can teleport in a single use.")
                .defineInRange("teleportMaxDistance", 50, 1, 1000);
        vaRageEnabled = builder
                .comment("When enabled, vampires can use the rage action.")
                .define("rageEnabled", true);
        vaRageCooldown = builder
                .comment("Cooldown for the vampire rage action, in seconds.")
                .defineInRange("rageCooldown", 20, 0, Integer.MAX_VALUE);
        vaRageMinDuration = builder
                .comment("Minimum duration of the vampire rage action, in seconds.")
                .defineInRange("rageMinDuration", 13, 1, 10000);
        vaRageDurationIncrease = builder
                .comment("Duration added to the vampire rage action per vampire level, in seconds.")
                .defineInRange("rageDurationIncrease", 5, 0, 1000);
        vaSunscreenEnabled = builder
                .comment("When enabled, vampires can use the sunscreen action.")
                .define("sunscreenEnabled", true);
        vaSunscreenCooldown = builder
                .comment("Cooldown for the vampire sunscreen action, in seconds.")
                .defineInRange("sunscreenCooldown", 500, 0, 1000);
        vaSunscreenDuration = builder
                .comment("Duration of the vampire sunscreen action, in seconds.")
                .defineInRange("sunscreenDuration", 40, 1, Integer.MAX_VALUE);
        vaBatEnabled = builder
                .comment("When enabled, vampires can transform into a bat.")
                .define("batEnabled", true);
        vaBatCooldown = builder
                .comment("Cooldown before a vampire can transform into a bat again, in seconds.")
                .defineInRange("batCooldown", 0, 0, 10000);
        vaBatDuration = builder
                .comment("Maximum duration of bat form, in seconds.")
                .defineInRange("batDuration", Integer.MAX_VALUE, 10, Integer.MAX_VALUE);
        vaBatHealthReduction = builder
                .comment("Factor by which the player's health is reduced upon entering bat form.")
                .defineInRange("batHealthReduction", 0.9, 0, 0.95);
        vaBatExhaustion = builder
                .comment("Additional blood exhaustion added per tick while in bat form. For reference, Haste I adds 0.01 per tick.")
                .defineInRange("batExhaustion", 0.005f, 0, 0.05);
        vaBatFlightSpeed = builder
                .comment("Movement speed of the player while in bat form.")
                .defineInRange("batFlightSpeed", 0.025f, 0.001, 0.2);
        vaBatAllowInteraction = builder
                .comment("When enabled, players in bat form can interact with blocks and items, and place or break blocks.")
                .define("batAllowInteraction", false);
        vaSummonBatsEnabled = builder
                .comment("When enabled, vampires can use the summon bats action.")
                .define("summonBatsEnabled", true);
        vaSummonBatsCooldown = builder
                .comment("Cooldown for the vampire summon bats action, in seconds.")
                .defineInRange("summonBatsCooldown", 300, 1, 10000);
        vaSummonBatsCount = builder
                .comment("Number of bats summoned by the vampire summon bats action.")
                .defineInRange("summonBatsCount", 16, 1, 100);
        vaDisguiseEnabled = builder
                .comment("When enabled, vampires can use the disguise action.")
                .define("disguiseEnabled", true);
        vaDisguiseCooldown = builder
                .comment("Cooldown for the vampire disguise action, in seconds.")
                .defineInRange("disguiseCooldown", 60, 1, 10000);
        vaDisguiseDuration = builder
                .comment("Duration of the vampire disguise action, in seconds.")
                .defineInRange("disguiseDuration", 60, 1, 10000);
        vaDarkBloodProjectileEnabled = builder
                .comment("When enabled, vampires can use the dark blood projectile action.")
                .define("darkBloodProjectileEnabled", true);
        vaDarkBloodProjectileCooldown = builder
                .comment("Cooldown for the vampire dark blood projectile action, in seconds.")
                .defineInRange("darkBloodProjectileCooldown", 4, 1, 1000);
        vaDarkBloodProjectileDamage = builder
                .comment("Damage dealt by the vampire dark blood projectile.")
                .defineInRange("darkBloodProjectileDamage", 6d, 0, 10000);
        vaHalfInvulnerableEnabled = builder
                .comment("When enabled, vampires can use the partial invulnerability action.")
                .define("halfInvulnerableEnabled", true);
        vaHalfInvulnerableCooldown = builder
                .comment("Cooldown for the vampire partial invulnerability action, in seconds.")
                .defineInRange("halfInvulnerableCooldown", 60, 1, 10000);
        vaHalfInvulnerableDuration = builder
                .comment("Duration of the vampire partial invulnerability action, in seconds.")
                .defineInRange("halfInvulnerableDuration", 30, 1, 10000);
        vaHalfInvulnerableThreshold = builder
                .comment("Damage threshold relative to the player's max health. Hits above this threshold are negated while the action is active.")
                .defineInRange("halfInvulnerableThreshold", 0.4d, 0.0d, 1d);
        vaHalfInvulnerableBloodCost = builder
                .comment("Blood cost per blocked hit during the partial invulnerability action.")
                .defineInRange("halfInvulnerableBloodCost", 4, 0, 1000);
        vaHissingEnabled = builder
                .comment("When enabled, vampires can use the hissing action.")
                .define("hissingEnabled", true);
        vaHissingCooldown = builder
                .comment("Cooldown for the vampire hissing action, in seconds.")
                .defineInRange("hissingCooldown", 60, 0, 10000);
        vaJumpBoostEnabled = builder
                .define("jumpBoostEnabled", true);
        vaJumpBoostCooldown = builder
                .comment("In seconds")
                .defineInRange("jumpBoostCooldown", 0, 0, 10000);
        vaJumpBoostDuration = builder
                .comment("In seconds")
                .defineInRange("jumpBoostDuration", Integer.MAX_VALUE, 10, Integer.MAX_VALUE);
        vaDarkStalkerEnabled = builder
                .comment("When enabled, vampires can use the dark stalker action.")
                .define("darkStalkerEnabled", true);
        vaDarkStalkerCooldown = builder
                .comment("Cooldown for the vampire dark stalker action, in ticks.")
                .defineInRange("darkStalkerCooldown", 1200, 1, Integer.MAX_VALUE);
        vaDarkStalkerDuration = builder
                .comment("Duration of the vampire dark stalker action, in ticks.")
                .defineInRange("darkStalkerDuration", 600, 1, Integer.MAX_VALUE);
        vaDarkStalkerBloodConsumption = builder
                .comment("Blood consumed per tick while the dark stalker action is active, in mB.")
                .defineInRange("darkStalkerBloodConsumption", 1, 0.0, 1000);

        builder.category("minions", "mi");

        miResourceCooldown = builder
                .comment("Cooldown in ticks before new resources are generated by a minion's collect resource task.")
                .defineInRange("resourceCooldown", 1500, 20, Integer.MAX_VALUE);

        builder.category("vampireRefinements", "vr");

        vrSwordTrainingSpeedMod = builder
                .comment("Multiplier applied to sword training speed with the sword training refinement.")
                .defineInRange("swordTrainingSpeedMod", 1.2D, 1D, Integer.MAX_VALUE);
        vrBloodChargeSpeedMod = builder
                .comment("Multiplier applied to blood charge speed with the blood charge refinement.")
                .defineInRange("bloodChargeSpeedMod", 3, 2, Integer.MAX_VALUE);
        vrFreezeDurationMod = builder
                .comment("Multiplier applied to freeze duration with the freeze refinement.")
                .defineInRange("freezeDurationMod", 1.4D, 1D, Integer.MAX_VALUE);
        vrVistaMod = builder
                .comment("Multiplier applied to the vampire's field of view with the vista refinement.")
                .defineInRange("vistaMod", 1D, 0D, 10D);
        vrDarkBloodProjectileDamageMod = builder
                .comment("Multiplier applied to dark blood projectile damage with the projectile damage refinement.")
                .defineInRange("darkBloodProjectileDamageMod", 1.5D, 1D, Integer.MAX_VALUE);
        vrDarkBloodProjectileAOECooldownMod = builder
                .comment("Multiplier applied to the dark blood projectile AOE cooldown with the AOE refinement.")
                .defineInRange("darkBloodProjectileAOECooldownMod", 2D, 1D, Integer.MAX_VALUE);
        vrDarkBloodProjectileAOERange = builder
                .comment("Squared radius of the dark blood projectile AOE effect with the AOE refinement.")
                .defineInRange("darkBloodProjectileAOERange", 16, 0, Integer.MAX_VALUE);
        vrSunscreenDurationMod = builder
                .comment("Multiplier applied to sunscreen action duration with the sunscreen refinement.")
                .defineInRange("sunscreenDurationMod", 1.5D, 1, Double.MAX_VALUE);
        vrRageFuryDurationBonus = builder
                .comment("Duration added to the rage action per kill while the fury refinement is active, in seconds.")
                .defineInRange("rageFuryDurationBonus", 5, 0, Integer.MAX_VALUE);
        vrTeleportDistanceMod = builder
                .comment("Multiplier applied to teleport distance with the teleport range refinement.")
                .defineInRange("teleportDistanceMod", 1.5, 1, Double.MAX_VALUE);
        vrTeleportCooldownMod = builder
                .defineInRange("teleportCooldownMod", 0.5, 0, Double.MAX_VALUE);
        vrHalfInvulnerableThresholdMod = builder
                .comment("Multiplier applied to the partial invulnerability damage threshold with the threshold refinement.")
                .defineInRange("halfInvulnerableThresholdMod", 0.7, 0, 2);
        vrSwordFinisherThresholdMod = builder
                .comment("Multiplier applied to the sword finisher health threshold with the finisher refinement.")
                .defineInRange("swordFinisherThresholdMod", 1.25, 1, Double.MAX_VALUE);

        builder.category("items", "it");

        itApplicableOilArmorReverse = builder
                .comment(String.format("When enabled, the '%s' item tag acts as a whitelist instead of a blacklist for armor oil application.", ModItemTags.APPLICABLE_OIL_ARMOR.location()))
                .define("applicableOilArmorReverse", false);
        itApplicableOilPickaxeReverse = builder
                .comment(String.format("When enabled, the '%s' item tag acts as a whitelist instead of a blacklist for pickaxe oil application.", ModItemTags.APPLICABLE_OIL_PICKAXE.location()))
                .define("applicableOilPickaxeReverse", false);
        itApplicableOilSwordReverse = builder
                .comment(String.format("When enabled, the '%s' item tag acts as a whitelist instead of a blacklist for sword oil application.", ModItemTags.APPLICABLE_OIL_SWORD.location()))
                .define("applicableOilSwordReverse", false);

        builder.category("effects", "ef");

        efExposedPerLevelMultiplier = builder
                .comment("Damage multiplier applied per level of the Exposed effect. For instance, a value of 0.25 means the entity will receive 25% more damage at level I, 50% at level II, 75% at level III, and so on.")
                .defineInRange("exposedPerLevelMultiplier", 0.25, 0.0, Double.MAX_VALUE);

        builder.category("lordActions", "la");

        laLordSpeedEnabled = builder
                .comment("When enabled, vampire lords can use the speed action.")
                .define("lordSpeedEnabled", true);
        laLordSpeedDuration = builder
                .comment("Duration of the vampire lord speed action, in seconds.")
                .defineInRange("lordSpeedDuration", 30, 0, Integer.MAX_VALUE);
        laLordSpeedCooldown = builder
                .comment("Cooldown for the vampire lord speed action, in seconds.")
                .defineInRange("lordSpeedCooldown", 120, 0, Integer.MAX_VALUE);
        laLordAttackSpeedEnabled = builder
                .comment("When enabled, vampire lords can use the attack speed action.")
                .define("lordAttackSpeedEnabled", true);
        laLordAttackSpeedDuration = builder
                .comment("Duration of the vampire lord attack speed action, in seconds.")
                .defineInRange("lordAttackSpeedDuration", 30, 0, Integer.MAX_VALUE);
        laLordAttackSpeedCooldown = builder
                .comment("Cooldown for the vampire lord attack speed action, in seconds.")
                .defineInRange("lordAttackSpeedCooldown", 120, 0, Integer.MAX_VALUE);
    }
}
