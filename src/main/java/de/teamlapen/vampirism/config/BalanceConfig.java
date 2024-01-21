package de.teamlapen.vampirism.config;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Values are null until after RegistryEvent<Block>
 */
public class BalanceConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    //GENERAL


    public final ModConfigSpec.BooleanValue canCancelSanguinare;
    public final ModConfigSpec.IntValue arrowVampireKillerMaxHealth;
    public final ModConfigSpec.IntValue holyWaterSplashDamage;
    public final ModConfigSpec.IntValue holyWaterNauseaDuration;
    public final ModConfigSpec.IntValue holyWaterBlindnessDuration;
    public final ModConfigSpec.IntValue dropOrchidFromLeavesChance;
    public final ModConfigSpec.DoubleValue holyWaterTierDamageInc;
    public final ModConfigSpec.DoubleValue vampireSwordChargingFactor;
    public final ModConfigSpec.DoubleValue vampireSwordBloodUsageFactor;
    public final ModConfigSpec.BooleanValue golemAttackNonVillageFaction;
    public final ModConfigSpec.BooleanValue zombieIgnoreVampire;
    public final ModConfigSpec.BooleanValue skeletonIgnoreVampire;
    public final ModConfigSpec.BooleanValue creeperIgnoreVampire;
    public final ModConfigSpec.DoubleValue bleedingEffectDamage;

    public final ModConfigSpec.IntValue hunterTentMaxSpawn;
    public final ModConfigSpec.DoubleValue crossbowDamageMult;
    public final ModConfigSpec.IntValue taskMasterMaxTaskAmount;
    public final ModConfigSpec.IntValue taskDurationSinglePlayer;
    public final ModConfigSpec.IntValue taskDurationDedicatedServer;
    public final ModConfigSpec.DoubleValue skillPointsPerLevel;
    public final ModConfigSpec.DoubleValue skillPointsPerLordLevel;
    public final ModConfigSpec.BooleanValue allowInfiniteSpecialArrows;
    public final ModConfigSpec.IntValue garlicDiffuserStartupTime;

    public final ModConfigSpec.DoubleValue eaHealthThreshold;
    public final ModConfigSpec.IntValue eaInvisibilityCooldown;
    public final ModConfigSpec.IntValue eaInvisibilityDuration;
    public final ModConfigSpec.IntValue eaHealCooldown;
    public final ModConfigSpec.IntValue eaHealAmount;
    public final ModConfigSpec.IntValue eaRegenerationDuration;
    public final ModConfigSpec.IntValue eaRegenerationCooldown;
    public final ModConfigSpec.IntValue eaRegenerationAmount;
    public final ModConfigSpec.IntValue eaSpeedDuration;
    public final ModConfigSpec.IntValue eaSpeedCooldown;
    public final ModConfigSpec.DoubleValue eaSpeedAmount;
    public final ModConfigSpec.IntValue eaBatspawnCooldown;
    public final ModConfigSpec.IntValue eaBatspawnAmount;
    public final ModConfigSpec.IntValue eaDarkProjectileCooldown;
    public final ModConfigSpec.DoubleValue eaDarkProjectileDamage;
    public final ModConfigSpec.DoubleValue eaDarkProjectileIndirectDamage;
    public final ModConfigSpec.IntValue eaSunscreenDuration;
    public final ModConfigSpec.IntValue eaSunscreenCooldown;
    public final ModConfigSpec.IntValue eaIgnoreSundamageDuration;
    public final ModConfigSpec.IntValue eaIgnoreSundamageCooldown;
    public final ModConfigSpec.IntValue eaGarlicDuration;
    public final ModConfigSpec.IntValue eaGarlicCooldown;

    public final ModConfigSpec.DoubleValue haDisguiseVisibilityMod;
    public final ModConfigSpec.BooleanValue haDisguiseEnabled;
    public final ModConfigSpec.IntValue haDisguiseInvisibleSQ;
    public final ModConfigSpec.IntValue haAwarenessDuration;
    public final ModConfigSpec.IntValue haAwarenessCooldown;
    public final ModConfigSpec.BooleanValue haAwarenessEnabled;
    public final ModConfigSpec.IntValue haAwarenessRadius;
    public final ModConfigSpec.IntValue haPotionResistanceDuration;
    public final ModConfigSpec.IntValue haPotionResistanceCooldown;
    public final ModConfigSpec.BooleanValue haPotionResistanceEnabled;

    public final ModConfigSpec.DoubleValue hpStrengthMaxMod;
    public final ModConfigSpec.DoubleValue hpStrengthType;

    public final ModConfigSpec.DoubleValue hsSmallAttackSpeedModifier;
    public final ModConfigSpec.DoubleValue hsSmallAttackDamageModifier;
    public final ModConfigSpec.BooleanValue hsInstantKill1FromBehind;
    public final ModConfigSpec.DoubleValue hsInstantKill1MaxHealth;
    public final ModConfigSpec.IntValue hsInstantKill2MaxHealth;
    public final ModConfigSpec.BooleanValue hsInstantKill2OnlyNPC;
    public final ModConfigSpec.IntValue hsGarlicDiffuserNormalDist;
    public final ModConfigSpec.IntValue hsGarlicDiffuserEnhancedDist;
    public final ModConfigSpec.IntValue hsGarlicDiffuserWeakDist;

    public final ModConfigSpec.IntValue viPhase1Duration;
    public final ModConfigSpec.IntValue viNotifyDistanceSQ;
    public final ModConfigSpec.IntValue viForceTargetTime;
    public final ModConfigSpec.IntValue viMaxVillagerRespawn;
    public final ModConfigSpec.IntValue viMaxTotemRadius;
    public final ModConfigSpec.DoubleValue viRandomRaidChance;

    public final ModConfigSpec.DoubleValue vsSundamageReduction1;
    public final ModConfigSpec.DoubleValue vsBloodThirstReduction1;
    public final ModConfigSpec.DoubleValue vsSwordFinisherMaxHealth;
    public final ModConfigSpec.IntValue vsJumpBoost;
    public final ModConfigSpec.DoubleValue vsSpeedBoost;
    public final ModConfigSpec.IntValue vsBloodVisionDistanceSq;
    public final ModConfigSpec.DoubleValue vsSmallAttackDamageModifier;
    public final ModConfigSpec.DoubleValue vsSmallAttackSpeedModifier;
    public final ModConfigSpec.DoubleValue vsNeonatalReduction;
    public final ModConfigSpec.DoubleValue vsDbnoReduction;

    public final ModConfigSpec.DoubleValue vpHealthMaxMod;
    public final ModConfigSpec.DoubleValue vpAttackSpeedMaxMod;
    public final ModConfigSpec.DoubleValue vpSpeedMaxMod;
    public final ModConfigSpec.DoubleValue vpExhaustionMaxMod;
    public final ModConfigSpec.DoubleValue vpBloodExhaustionFactor;
    public final ModConfigSpec.BooleanValue vpBloodUsagePeaceful;
    public final ModConfigSpec.DoubleValue vpPlayerBloodSaturation;
    public final ModConfigSpec.IntValue vpSanguinareAverageDuration;
    public final ModConfigSpec.IntValue vpSundamageMinLevel;
    public final ModConfigSpec.BooleanValue vpSundamageNausea;
    public final ModConfigSpec.IntValue vpSundamageNauseaMinLevel;
    public final ModConfigSpec.IntValue vpSundamageWeaknessMinLevel;
    public final ModConfigSpec.DoubleValue vpSundamage;
    public final ModConfigSpec.IntValue vpSundamageWaterblocks;
    public final ModConfigSpec.BooleanValue vpSundamageInstantDeath;
    public final ModConfigSpec.BooleanValue vpSunscreenBuff;
    public final ModConfigSpec.DoubleValue vpFireVulnerabilityMod;
    public final ModConfigSpec.BooleanValue vpFireResistanceReplace;
    public final ModConfigSpec.IntValue vpMaxYellowBorderPercentage;
    public final ModConfigSpec.ConfigValue<List<? extends String>> vpImmortalFromDamageSources;
    public final ModConfigSpec.IntValue vpDbnoDuration;
    public final ModConfigSpec.IntValue vpNeonatalDuration;
    public final ModConfigSpec.IntValue vpNaturalArmorRegenDuration;
    public final ModConfigSpec.IntValue vpNaturalArmorBaseValue;
    public final ModConfigSpec.IntValue vpNaturalArmorIncrease;
    public final ModConfigSpec.IntValue vpNaturalArmorToughnessIncrease;
    public final ModConfigSpec.BooleanValue vpArmorPenalty;
    public final ModConfigSpec.BooleanValue vpNightVisionDisabled;
    public final ModConfigSpec.BooleanValue vpBloodVisionDisabled;


    public final ModConfigSpec.IntValue vaFreezeCooldown;
    public final ModConfigSpec.BooleanValue vaFreezeEnabled;
    public final ModConfigSpec.IntValue vaFreezeDuration;
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

    public final ModConfigSpec.IntValue miResourceCooldown;
    public final ModConfigSpec.DoubleValue miResourceCooldownOfflineMult;
    public final ModConfigSpec.IntValue miDeathRecoveryTime;
    public final ModConfigSpec.IntValue miMinionPerLordLevel;
    public final ModConfigSpec.IntValue miEquipmentRepairAmount;

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
    public final ModConfigSpec.DoubleValue vrHalfInvulnerableThresholdMod;
    public final ModConfigSpec.DoubleValue vrSwordFinisherThresholdMod;

    public final ModConfigSpec.BooleanValue itApplicableOilArmorReverse;
    public final ModConfigSpec.BooleanValue itApplicableOilPickaxeReverse;
    public final ModConfigSpec.BooleanValue itApplicableOilSwordReverse;

    public final ModConfigSpec.BooleanValue laLordSpeedEnabled;
    public final ModConfigSpec.IntValue laLordSpeedCooldown;
    public final ModConfigSpec.IntValue laLordSpeedDuration;

    public final ModConfigSpec.BooleanValue laLordAttackSpeedEnabled;
    public final ModConfigSpec.IntValue laLordAttackSpeedCooldown;
    public final ModConfigSpec.IntValue laLordAttackSpeedDuration;

    BalanceConfig(@NotNull BalanceBuilder builder) {
        boolean iceAndFire = ModList.get().isLoaded("iceandfire");
        if (iceAndFire) {
            LOGGER.info("IceAndFire is loaded -> Adjusting default fire related configuration.");
        }

        //This is build using the intermediate builder to allow modification by addon mods.
        //It is finalized and assigned during RegistryEvent<Block>

        //GENERAL
        builder.comment("General options");
        builder.category("general", "");


        canCancelSanguinare = builder.comment("If the sanguinare effect can be canceled by milk").define("canCancelSanguinare", true);
        arrowVampireKillerMaxHealth = builder.comment("The vampire killer arrow can only instant kill NPC vampires that have a max (not actual) health of this").defineInRange("arrowVampireKillerMaxHealth", 40, 1, Integer.MAX_VALUE);
        holyWaterSplashDamage = builder.comment("Damage a normal holy water splash bottle does when directly hitting a vampire").defineInRange("holyWaterSplashDamage", 5, 0, Integer.MAX_VALUE);
        holyWaterTierDamageInc = builder.comment("Holy water damage is multiplied with this value for each tier above normal").defineInRange("holyWaterTierDamageInc", 2d, 1d, 10d);
        holyWaterNauseaDuration = builder.comment("Duration of the nausea effect caused by enhanced or special holy water (ticks)").defineInRange("holyWaterNauseaDuration", 200, 0, 1000);
        holyWaterBlindnessDuration = builder.comment("Duration of the blindness effect caused by special holy water (ticks)").defineInRange("holyWaterBlindnessDuration", 160, 0, 1000);
        vampireSwordChargingFactor = builder.comment("The blood mB to charge percentage of the normal vampire sword").defineInRange("vampireSwordChargingFactor", 0.05 / (double) VReference.FOOD_TO_FLUID_BLOOD, 0d, 1d);
        vampireSwordBloodUsageFactor = builder.comment("The percentage of stored blood used for every hit with a normal vampire sword").defineInRange("vampireSwordBloodUsageFactor", 0.5, 0, 100d);
        dropOrchidFromLeavesChance = builder.comment("Drop orchid every n times breaking a leave in the vampire forest").defineInRange("dropOrchidFromLeavesChance", 25, 1, Integer.MAX_VALUE);
        golemAttackNonVillageFaction = builder.comment("If iron golems should attack faction NPCs if in a village with a different faction").define("golemAttackNonVillageFaction", true);
        zombieIgnoreVampire = builder.comment("Whether zombies should ignore vampires").define("zombieIgnoreVampire", true);
        skeletonIgnoreVampire = builder.comment("Whether skeletons should ignore vampires").define("skeletonIgnoreVampire", true);
        creeperIgnoreVampire = builder.comment("Whether creepers should ignore vampires").define("creeperIgnoreVampire", true);
        hunterTentMaxSpawn = builder.comment("Maximum number of hunters that can spawn at one tent per day").defineInRange("hunterTentMaxSpawn", 4, 0, 20);
        crossbowDamageMult = builder.comment("The base damage dealt by crossbow arrows is multiplied by this").defineInRange("crossbowDamageMult", 1, 0.2, 5);
        taskMasterMaxTaskAmount = builder.comment("Maximum amount of task shown at a taskmaster, except unique tasks").defineInRange("taskMasterMaxTaskAmount", 3, 1, Integer.MAX_VALUE);
        taskDurationSinglePlayer = builder.comment("Duration a task can be completed in a singleplayer world. In Minutes").defineInRange("taskDurationSinglePlayer", 120, 1, Integer.MAX_VALUE);
        taskDurationDedicatedServer = builder.comment("Duration a task can be completed on a dedicated server. In Minutes").defineInRange("taskDurationDedicatedServer", 1440, 1, Integer.MAX_VALUE);
        skillPointsPerLevel = builder.comment("Players receive n skill points for each level-up. Anything except 2 is unbalanced, but to unlock all skills on maxlevel this value should be set to skill-amount/(max-level - 1)").defineInRange("skillPointsPerLevel", 2D, 1D, 20D);
        skillPointsPerLordLevel = builder.comment("Players receive n skill points for each lord level-up. Anything except 2 is unbalanced, but to unlock all skills on max lord level this value should be set to skill-amount/(max-level - 1)").defineInRange("skillPointsPerLordLevel", 2D, 1D, 20D);
        allowInfiniteSpecialArrows = builder.comment("Whether special crossbow arrows (e.g. spitfire) can be used with infinity enchantment").define("allowInfiniteSpecialArrows", false);
        garlicDiffuserStartupTime = builder.comment("Delay in seconds before a newly placed garlic diffuser becomes active. *0.25 in Singleplayer").defineInRange("garlicDiffuserStartupTime", 5 * 20, 1, 10000);
        bleedingEffectDamage = builder.comment("How much damage the bleeding effect should do per damaging tick").defineInRange("bleedingEffectDamage", 0.1, 0, Double.MAX_VALUE);

        //Entity actions
        builder.category("entityActions", "ea");
        eaHealthThreshold = builder.comment("Relative health a entity must have to use actions").defineInRange("healthThreshold", 0.3, 0, 1);
        eaInvisibilityCooldown = builder.comment("In seconds").defineInRange("invisibilityCooldown", 7, 1, Integer.MAX_VALUE);
        eaInvisibilityDuration = builder.comment("In seconds").defineInRange("invisibilityDuration", 4, 1, Integer.MAX_VALUE);
        eaHealAmount = builder.comment("In percent").defineInRange("healAmount", 30, 0, 100);
        eaHealCooldown = builder.comment("In seconds").defineInRange("healCooldown", 7, 1, Integer.MAX_VALUE);
        eaRegenerationDuration = builder.comment("In seconds").defineInRange("regenerationDuration", 5, 0, Integer.MAX_VALUE);
        eaRegenerationAmount = builder.comment("In percent").defineInRange("regenerationAmount", 40, 0, 100);
        eaRegenerationCooldown = builder.comment("In seconds").defineInRange("regenerationCooldown", 8, 0, Integer.MAX_VALUE);
        eaSpeedDuration = builder.comment("In seconds").defineInRange("speedDuration", 4, 0, Integer.MAX_VALUE);
        eaSpeedCooldown = builder.comment("In seconds").defineInRange("speedCooldown", 6, 1, Integer.MAX_VALUE);
        eaSpeedAmount = builder.comment("Speed = basevalue * (1+ speedAmount)").defineInRange("speedAmount", 0.14, 0, 2);
        eaBatspawnAmount = builder.defineInRange("batspawnAmount", 4, 1, 10);
        eaBatspawnCooldown = builder.comment("In seconds").defineInRange("batspawnCooldown", 15, 1, Integer.MAX_VALUE);
        eaDarkProjectileCooldown = builder.comment("In seconds").defineInRange("darkProjectileCooldown", 10, 1, Integer.MAX_VALUE);
        eaDarkProjectileDamage = builder.defineInRange("darkProjectileDamage", 5d, 0, 100);
        eaDarkProjectileIndirectDamage = builder.defineInRange("darkProjectileIndirectDamage", 2d, 0, 100);
        eaSunscreenDuration = builder.comment("In seconds").defineInRange("sunscreenDuration", 9, 0, Integer.MAX_VALUE);
        eaSunscreenCooldown = builder.comment("In seconds").defineInRange("sunscreenCooldown", 10, 1, Integer.MAX_VALUE);
        eaIgnoreSundamageCooldown = builder.comment("In seconds").defineInRange("ignoreSundamageCooldown", 6, 1, Integer.MAX_VALUE);
        eaIgnoreSundamageDuration = builder.comment("In seconds").defineInRange("ignoreSundamageDuration", 5, 0, Integer.MAX_VALUE);
        eaGarlicCooldown = builder.comment("In seconds").defineInRange("garlicCooldown", 5, 1, Integer.MAX_VALUE);
        eaGarlicDuration = builder.comment("In seconds").defineInRange("garlicDuration", 5, 0, Integer.MAX_VALUE);

        //Hunter actions
        builder.category("hunterActions", "ha");
        haDisguiseEnabled = builder.define("disguiseEnabled", true);
        haDisguiseVisibilityMod = builder.comment("If disguised the detection radius of mobs will be multiplied by this").defineInRange("disguiseVisibilityMod", 0.2D, 0, 1);
        haDisguiseInvisibleSQ = builder.comment("Squared distance as of which a disguised hunter is invisible").defineInRange("disguiseInvisibleSQ", 256, 1, Integer.MAX_VALUE);
        haAwarenessEnabled = builder.define("awarenessEnabled", true);
        haAwarenessDuration = builder.comment("In ticks").defineInRange("awarenessDuration", 300, 1, Integer.MAX_VALUE);
        haAwarenessCooldown = builder.comment("In ticks").defineInRange("awarenessCooldown", 900, 1, Integer.MAX_VALUE);
        haAwarenessRadius = builder.comment("Radius in which vampires should be detected").defineInRange("awarenessRadius", 25, 0, 50);
        haPotionResistanceEnabled = builder.define("potionResistanceEnabled", true);
        haPotionResistanceDuration = builder.comment("In ticks").defineInRange("potionResistanceDuration", 400, 1, Integer.MAX_VALUE);
        haPotionResistanceCooldown = builder.comment("In ticks").defineInRange("potionResistanceCooldown", 1200, 1, Integer.MAX_VALUE);

        //Hunter player
        builder.category("hunterPlayer", "hp");
        hpStrengthMaxMod = builder.comment("Strength = Old * (modifier+1").defineInRange("strengthMaxMod", 0.3d, 0d, 4d);
        hpStrengthType = builder.comment("0.5 for square root, 1 for linear").defineInRange("strengthType", 0.5d, 0.5d, 1);

        //Hunter skills
        builder.category("hunterSkills", "hs");
        hsSmallAttackSpeedModifier = builder.comment("Basic skill - Weapon cooldown = 1/(oldvalue*(1+modifier))").defineInRange("smallAttackSpeedModifier", 0.3, 0, 3);
        hsSmallAttackDamageModifier = builder.comment("Increase damage - Added to base damage").defineInRange("smallAttackDamageModifier", 0.1d, 0, 10);
        hsInstantKill1FromBehind = builder.comment("First stake skill - If it is required to attack from behind to instant kill low level vampires").define("instantKill1FromBehind", false);
        hsInstantKill1MaxHealth = builder.comment("First stake skill -The maximal relative health a entity may have to be instantly killed").defineInRange("instantKill1MaxHealth", 0.35, 0, 1);
        hsInstantKill2MaxHealth = builder.comment("Second stake skill - The max (not the actual) health of an entity that can be one hit killed from behind").defineInRange("instantKill2MaxHealth", 200, 0, Integer.MAX_VALUE);
        hsInstantKill2OnlyNPC = builder.comment("Second stake skill - Whether only NPCs can be one hit killed with this skill").define("instantKill2OnlyNPC", true);
        hsGarlicDiffuserNormalDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffuserNormalDist", 0, 0, 5);
        hsGarlicDiffuserEnhancedDist = builder.comment("The chunk radius a enhanced diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffuserEnhancedDist", 1, 0, 5);
        hsGarlicDiffuserWeakDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffuserWeakDist", 2, 0, 5);

        //Village
        builder.category("village", "vi");
        viPhase1Duration = builder.comment("Duration of phase 1 of the capturing process in 2*seconds").defineInRange("phase1Duration", 80, 1, 1000);
        viNotifyDistanceSQ = builder.comment("Squared distance of village capture notification").defineInRange("notifyDistanceSQ", 40000, 0, 100000);
        viForceTargetTime = builder.comment("Time in 2*seconds in capture phase 2 after which the capture entities should find a target regardless of distance").defineInRange("forceTargetTime", 80, 1, 1000);
        viMaxVillagerRespawn = builder.comment("Maximum of Villager the Totem can respawn").defineInRange("maxVillagerRespawn", 30, 0, Integer.MAX_VALUE);
        viMaxTotemRadius = builder.comment("Maximum range of a Totem to grow the village").defineInRange("maxTotemRadius", 100, 0, Integer.MAX_VALUE);
        viRandomRaidChance = builder.comment("Chance (per tick) of a faction raid to occur").defineInRange("randomRaidChance", 0.000138888888889, 0, 1);


        //Vampire skills
        builder.category("vampireSkills", "vs");
        vsSundamageReduction1 = builder.comment("Sundamage is multiplied with (value+1)").defineInRange("sundamageReduction1", -0.5, -1, 0);
        vsBloodThirstReduction1 = builder.comment("Blood exhaustion is multiplied with (value+1)").defineInRange("bloodThirstReduction1", -0.4, -1, 0);
        vsSwordFinisherMaxHealth = builder.comment("The max relative health for sword finisher kill").defineInRange("swordFinisherMaxHealth", 0.25, 0, 1);
        vsJumpBoost = builder.comment("Similar to potion effect amplifier (and -1 is normal)").defineInRange("jumpBoost", 1, -1, 5);
        vsSpeedBoost = builder.comment("Max speed is multiplied with (value+1)").defineInRange("speedBoost", 0.15, 0, 3);
        vsBloodVisionDistanceSq = builder.comment("Squared blood vision distance").defineInRange("bloodVisionDistanceSq", 1600, 5, Integer.MAX_VALUE);
        vsSmallAttackDamageModifier = builder.comment("Damage added to base damage").defineInRange("smallAttackDamageModifier", 1d, 0, 10d);
        vsSmallAttackSpeedModifier = builder.comment("Basic skill - Weapon cooldown = 1/(oldvalue*(1+modifier))").defineInRange("smallAttackSpeedModifier", 0.15, 0, 3);
        vsNeonatalReduction = builder.comment("Reduced percentage of the neonatal effect").defineInRange("neonatalReduction", 0.5, 0, 1024);
        vsDbnoReduction = builder.comment("Reduced percentage of the downed timer required to resurrect").defineInRange("dbnoReduction", 0.5, 0, 1024);


        //Vampire Player TODO 1.19 rename *MaxMod to *MaxLevelMod and clarify whether it is a multiplicative or additive modifier
        builder.category("vampirePlayer", "vp");
        vpHealthMaxMod = builder.defineInRange("healthMaxMod", 16, 0.5, 40);
        vpAttackSpeedMaxMod = builder.defineInRange("attackSpeedMaxMod", 0.15, 0, 2);
        vpSpeedMaxMod = builder.defineInRange("speedMaxMod", 0.3, 0, 5);
        vpExhaustionMaxMod = builder.defineInRange("exhaustionMaxMod", 1.0, 0, 10);
        vpBloodExhaustionFactor = builder.comment("Blood exhaustion is multiplied with this value").defineInRange("bloodExhaustionFactor", 0.7, 0, 5);
        vpBloodUsagePeaceful = builder.comment("Whether blood is consumed in peaceful gamemode").define("bloodUsagePeaceful", false);
        vpPlayerBloodSaturation = builder.defineInRange("playerBloodSaturation", 1.5, 0.3, 10);
        vpSanguinareAverageDuration = builder.comment("Average duration of the Sanguinare Vampiris Effect. The final duration is random between 0.5 x avgDuration - 1.5 x avgDuration. In Seconds.").defineInRange("sanguinareAverageDuration", 900, 1, 10000);
        vpSundamage = builder.defineInRange("sundamage", 7d, 1, Double.MAX_VALUE);
        vpSundamageMinLevel = builder.defineInRange("sundamageMinLevel", 4, 1, Integer.MAX_VALUE);
        vpSundamageNausea = builder.comment("Weather a vampire player that receives sundamage should also receive a nausea effect").define("sundamageNausea", true);
        vpSundamageNauseaMinLevel = builder.defineInRange("sundamageNauseaMinLevel", 3, 1, Integer.MAX_VALUE);
        vpSundamageWeaknessMinLevel = builder.defineInRange("sundamageWeaknessMinLevel", 2, 1, Integer.MAX_VALUE);
        vpSundamageWaterblocks = builder.defineInRange("sundamageWaterblocks", 4, 1, 10);
        vpSundamageInstantDeath = builder.comment("Whether vampires are instantly turned into ash when being in the sun").define("sundamageInstantDeath", false);
        vpSunscreenBuff = builder.comment("Buff sunscreen potion to prevent negative effects at any level").define("sunscreenBuff", false);
        vpFireVulnerabilityMod = builder.comment("Multiply fire damage with this for vampires" + (iceAndFire ? " - Changed due to IceAndFire" : "")).defineInRange("fireVulnerabilityMod", iceAndFire ? 1.5d : 3d, 0.1, Double.MAX_VALUE);
        vpFireResistanceReplace = builder.comment("Whether to replace the vanilla fire resistance potion for vampires with a custom one that only reduces damage but does not remove it" + (iceAndFire ? " - Changed due to IceAndFire" : "")).define("fireResistanceReplace", !iceAndFire);
        vpMaxYellowBorderPercentage = builder.comment("Defines the maximum extend the yellow border covers when the player is in the sun. 100 is default. 0 to disable completely").defineInRange("maxYellowBorderPercentage", 100, 0, 100);
        vpImmortalFromDamageSources = builder.comment("List of damage source types that the player does not die from (immediately)").defineList("immortalFromDamageSources", List.of(), s -> ResourceLocation.tryParse((String) s) != null);
        vpDbnoDuration = builder.comment("Base cooldown before a downed vampire can resurrect. In sec.").defineInRange("dbnoDuration", 60, 1, 1000);
        vpNeonatalDuration = builder.comment("Base duration of neonatal effect after resurrection. In sec.").defineInRange("neonatalDuration", 120, 1, Integer.MAX_VALUE);
        vpNaturalArmorRegenDuration = builder.comment("The duration it takes for the vampire natural armor to fully regenerate after respawn. In seconds").defineInRange("naturalArmorRegenDuration", 240, 1, 2400);
        vpNaturalArmorBaseValue = builder.comment("The base value of natural armor every vampire has at level 1").defineInRange("naturalArmorBaseValue", 10, 0, 100);
        vpNaturalArmorIncrease = builder.comment("The amount of natural armor a max level vampire has in addition to the base value").defineInRange("naturalArmorIncrease", 10, 0, 100);
        vpNaturalArmorToughnessIncrease = builder.comment("The amount of natural armor toughness a max level vampire has").defineInRange("naturalArmorToughnessIncrease", 8, 0, 100);
        vpArmorPenalty = builder.comment("Whether vampire have a reduced speed and attack boost when wearing heavy armor").define("armorPenalty", true);
        vpNightVisionDisabled = builder.comment("Disable vampire night vision").define("nightVisionDisabled", false);
        vpBloodVisionDisabled = builder.comment("Disable vampire blood vision").define("bloodVisionDisabled", false);


        //Vampire actions
        builder.category("vampireActions", "va");
        vaFreezeCooldown = builder.comment("In seconds").defineInRange("freezeCooldown", 60, 1, Integer.MAX_VALUE);
        vaFreezeDuration = builder.comment("In seconds").defineInRange("freezeDuration", 3, 1, 30);
        vaFreezeEnabled = builder.define("freezeEnabled", true);
        vaInvisibilityCooldown = builder.comment("In seconds").defineInRange("invisibilityCooldown", 25, 1, Integer.MAX_VALUE);
        vaInvisibilityDuration = builder.comment("In seconds").defineInRange("invisibilityDuration", 25, 1, Integer.MAX_VALUE);
        vaInvisibilityEnabled = builder.define("invisibilityEnabled", true);
        vaRegenerationCooldown = builder.comment("In seconds").defineInRange("regenerationCooldown", 60, 0, Integer.MAX_VALUE);
        vaRegenerationDuration = builder.comment("In seconds").defineInRange("regenerationDuration", 20, 0, Integer.MAX_VALUE);
        vaRegenerationEnabled = builder.define("regenerationEnabled", true);
        vaTeleportCooldown = builder.comment("In seconds").defineInRange("teleportCooldown", 10, 1, Integer.MAX_VALUE);
        vaTeleportMaxDistance = builder.defineInRange("teleportMaxDistance", 50, 1, 1000);
        vaTeleportEnabled = builder.define("teleportEnabled", true);
        vaRageCooldown = builder.comment("In seconds").defineInRange("rageCooldown", 20, 0, Integer.MAX_VALUE);
        vaRageMinDuration = builder.comment("In seconds").defineInRange("rageMinDuration", 13, 1, 10000);
        vaRageDurationIncrease = builder.comment("In seconds. Increase per vampire level").defineInRange("rageDurationIncrease", 5, 0, 1000);
        vaRageEnabled = builder.define("rageEnabled", true);
        vaSunscreenCooldown = builder.comment("In seconds").defineInRange("sunscreenCooldown", 500, 0, 1000);
        vaSunscreenDuration = builder.comment("In seconds").defineInRange("sunscreenDuration", 40, 1, Integer.MAX_VALUE);
        vaSunscreenEnabled = builder.define("sunscreenEnabled", true);
        vaBatEnabled = builder.define("batEnabled", true);
        vaBatCooldown = builder.comment("In seconds").defineInRange("batCooldown", 0, 0, 10000);
        vaBatDuration = builder.comment("In seconds").defineInRange("batDuration", Integer.MAX_VALUE, 10, Integer.MAX_VALUE);
        vaBatHealthReduction = builder.comment("The player health will be reduced by this factor").defineInRange("batHealthReduction", 0.9, 0, 0.95);
        vaBatExhaustion = builder.comment("Additional exhaustion added while in bat mode. E.g. Thirst I would be 0.01").defineInRange("batExhaustion", 0.005f, 0, 0.05);
        vaBatFlightSpeed = builder.defineInRange("batFlightSpeed", 0.025f, 0.001, 0.2);
        vaBatAllowInteraction = builder.comment("Whether to allow players in bat mode to interact with items/blocks and place/break blocks").define("batAllowInteraction", false);
        vaSummonBatsCooldown = builder.comment("In seconds").defineInRange("summonBatsCooldown", 300, 1, 10000);
        vaSummonBatsCount = builder.defineInRange("summonBatsCount", 16, 1, 100);
        vaSummonBatsEnabled = builder.define("summonBatsEnabled", true);
        vaDisguiseCooldown = builder.comment("In seconds").defineInRange("disguiseCooldown", 60, 1, 10000);
        vaDisguiseDuration = builder.comment("In seconds").defineInRange("disguiseDuration", 60, 1, 10000);
        vaDisguiseEnabled = builder.define("disguiseEnabled", true);
        vaDarkBloodProjectileCooldown = builder.comment("In seconds").defineInRange("darkBloodProjectileCooldown", 4, 1, 1000);
        vaDarkBloodProjectileDamage = builder.defineInRange("darkBloodProjectileDamage", 6d, 0, 10000);
        vaDarkBloodProjectileEnabled = builder.define("darkBloodProjectileEnabled", true);
        vaHalfInvulnerableCooldown = builder.defineInRange("halfInvulnerableCooldown", 60, 1, 10000);
        vaHalfInvulnerableDuration = builder.defineInRange("halfInvulnerableDuration", 30, 1, 10000);
        vaHalfInvulnerableThreshold = builder.comment("Damage threshold relative to players max health. Damage above this value will be ignored").defineInRange("halfInvulnerableThreshold", 0.4d, 0.0d, 1d);
        vaHalfInvulnerableBloodCost = builder.defineInRange("halfInvulnerableBloodCost", 4, 0, 1000);
        vaHalfInvulnerableEnabled = builder.define("halfInvulnerableEnabled", true);
        vaHissingCooldown = builder.comment("In seconds").defineInRange("hissingCooldown", 60, 0, 10000);
        vaHissingEnabled = builder.define("hissingEnabled", true);


        builder.category("minions", "mi");
        miResourceCooldown = builder.comment("Cooldown in ticks,before new resources are added in collect resource task types").defineInRange("resourceCooldown", 1500, 20, Integer.MAX_VALUE);
        miResourceCooldownOfflineMult = builder.comment("Cooldown multiplier for collect resource task types while player is offline").defineInRange("resourceCooldownOfflineMult", 20D, 1D, 100000D);
        miDeathRecoveryTime = builder.comment("Time in seconds a minion needs to recover from death.").defineInRange("deathRecoveryTime", 220, 1, Integer.MAX_VALUE / 100);
        miMinionPerLordLevel = builder.comment("How many minions a player can have per lord level. Probably don't want to go very high").defineInRange("minionPerLordLevel", 1, 0, 100);
        miEquipmentRepairAmount = builder.comment("How much the equipments should be repaired on minion resource tasks").defineInRange("equipmentRepairAmount", 10, 1, Integer.MAX_VALUE);

        builder.category("vampire_refinements", "vr");
        vrSwordTrainingSpeedMod = builder.defineInRange("swordTrainingSpeedMod", 1.2D, 1D, Integer.MAX_VALUE);
        vrBloodChargeSpeedMod = builder.defineInRange("bloodChargeSpeedMod", 3, 2, Integer.MAX_VALUE);
        vrFreezeDurationMod = builder.defineInRange("freezeDurationMod", 1.4D, 1D, Integer.MAX_VALUE);
        vrVistaMod = builder.defineInRange("vistaMod", 1D, 0D, 10D);
        vrDarkBloodProjectileDamageMod = builder.defineInRange("darkBloodProjectileDamageMod", 1.5D, 1D, Integer.MAX_VALUE);
        vrDarkBloodProjectileAOECooldownMod = builder.defineInRange("darkBloodProjectileAOECooldownMod", 2D, 1D, Integer.MAX_VALUE);
        vrDarkBloodProjectileAOERange = builder.comment("squared value").defineInRange("darkBloodProjectileAOERange", 16, 0, Integer.MAX_VALUE);
        vrSunscreenDurationMod = builder.defineInRange("sunscreenDurationMod", 1.5D, 1, Double.MAX_VALUE);
        vrRageFuryDurationBonus = builder.comment("For every kill the rage duration is extended by this amount. In seconds.").defineInRange("rageFuryDurationBonus", 5, 0, Integer.MAX_VALUE);
        vrTeleportDistanceMod = builder.defineInRange("teleportDistanceMod", 1.5, 1, Double.MAX_VALUE);
        vrHalfInvulnerableThresholdMod = builder.comment("Threshold for attacks that are considered high damage is multiplied by this value").defineInRange("halfInvulnerableThresholdMod", 0.7, 0, 2);
        vrSwordFinisherThresholdMod = builder.comment("Threshold for instant kill is modified by this amount").defineInRange("swordFinisherThresholdMod", 1.25, 1, Double.MAX_VALUE);

        builder.category("items", "it");
        itApplicableOilArmorReverse = builder.comment(String.format("Determines if the '%s' item tag should work as blacklist (false) or whitelist (true)", ModTags.Items.APPLICABLE_OIL_ARMOR.location())).define("applicableOilArmorReverse", false);
        itApplicableOilPickaxeReverse = builder.comment(String.format("Determines if the '%s' item tag should work as blacklist (false) or whitelist (true)", ModTags.Items.APPLICABLE_OIL_PICKAXE.location())).define("applicableOilPickaxeReverse", false);
        itApplicableOilSwordReverse = builder.comment(String.format("Determines if the '%s' item tag should work as blacklist (false) or whitelist (true)", ModTags.Items.APPLICABLE_OIL_SWORD.location())).define("applicableOilSwordReverse", false);

        builder.category("lord actions", "la");
        laLordSpeedEnabled = builder.define("lordSpeedEnabled", true);
        laLordSpeedDuration = builder.comment("In seconds").defineInRange("lordSpeedDuration", 30, 0, Integer.MAX_VALUE);
        laLordSpeedCooldown = builder.comment("In seconds").defineInRange("lordSpeedCooldown", 120, 0, Integer.MAX_VALUE);
        laLordAttackSpeedEnabled = builder.define("lordAttackSpeedEnabled", true);
        laLordAttackSpeedDuration = builder.comment("In seconds").defineInRange("lordAttackSpeedDuration", 30, 0, Integer.MAX_VALUE);
        laLordAttackSpeedCooldown = builder.comment("In seconds").defineInRange("lordAttackSpeedCooldown", 120, 0, Integer.MAX_VALUE);

    }
}
