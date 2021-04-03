package de.teamlapen.vampirism.config;


import de.teamlapen.vampirism.api.VReference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Values are null until after RegistryEvent<Block>
 */
public class BalanceConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    //GENERAL
    public final ForgeConfigSpec.IntValue hunterTentDistance;
    public final ForgeConfigSpec.IntValue hunterTentSeparation;
    public final ForgeConfigSpec.IntValue vampireForestWeight;
    public final ForgeConfigSpec.IntValue vampireForestHillsWeight;
    public final ForgeConfigSpec.BooleanValue canCancelSanguinare;
    public final ForgeConfigSpec.IntValue arrowVampireKillerMaxHealth;
    public final ForgeConfigSpec.IntValue holyWaterSplashDamage;
    public final ForgeConfigSpec.IntValue holyWaterNauseaDuration;
    public final ForgeConfigSpec.IntValue holyWaterBlindnessDuration;
    public final ForgeConfigSpec.IntValue dropOrchidFromLeavesChance;
    public final ForgeConfigSpec.DoubleValue holyWaterTierDamageInc;
    public final ForgeConfigSpec.DoubleValue vampireSwordChargingFactor;
    public final ForgeConfigSpec.DoubleValue vampireSwordBloodUsageFactor;
    public final ForgeConfigSpec.BooleanValue golemAttackNonVillageFaction;
    public final ForgeConfigSpec.BooleanValue zombieIgnoreVampire;
    public final ForgeConfigSpec.BooleanValue skeletonIgnoreVampire;
    public final ForgeConfigSpec.BooleanValue creeperIgnoreVampire;

    public final ForgeConfigSpec.IntValue hunterTentMaxSpawn;
    public final ForgeConfigSpec.DoubleValue crossbowDamageMult;
    public final ForgeConfigSpec.IntValue taskMasterMaxTaskAmount;
    public final ForgeConfigSpec.IntValue skillPointsPerLevel;
    public final ForgeConfigSpec.BooleanValue allowInfiniteSpecialArrows;

    public final ForgeConfigSpec.DoubleValue eaHealthThreshold;
    public final ForgeConfigSpec.IntValue eaInvisibilityCooldown;
    public final ForgeConfigSpec.IntValue eaInvisibilityDuration;
    public final ForgeConfigSpec.IntValue eaHealCooldown;
    public final ForgeConfigSpec.IntValue eaHealAmount;
    public final ForgeConfigSpec.IntValue eaRegenerationDuration;
    public final ForgeConfigSpec.IntValue eaRegenerationCooldown;
    public final ForgeConfigSpec.IntValue eaRegenerationAmount;
    public final ForgeConfigSpec.IntValue eaSpeedDuration;
    public final ForgeConfigSpec.IntValue eaSpeedCooldown;
    public final ForgeConfigSpec.DoubleValue eaSpeedAmount;
    public final ForgeConfigSpec.IntValue eaBatspawnCooldown;
    public final ForgeConfigSpec.IntValue eaBatspawnAmount;
    public final ForgeConfigSpec.IntValue eaDarkProjectileCooldown;
    public final ForgeConfigSpec.DoubleValue eaDarkProjectileDamage;
    public final ForgeConfigSpec.DoubleValue eaDarkProjectileIndirectDamage;
    public final ForgeConfigSpec.IntValue eaSunscreenDuration;
    public final ForgeConfigSpec.IntValue eaSunscreenCooldown;
    public final ForgeConfigSpec.IntValue eaIgnoreSundamageDuration;
    public final ForgeConfigSpec.IntValue eaIgnoreSundamageCooldown;
    public final ForgeConfigSpec.IntValue eaGarlicDuration;
    public final ForgeConfigSpec.IntValue eaGarlicCooldown;

    public final ForgeConfigSpec.DoubleValue haDisguiseVisibilityMod;
    public final ForgeConfigSpec.BooleanValue haDisguiseEnabled;
    public final ForgeConfigSpec.IntValue haDisguiseInvisibleSQ;
    public final ForgeConfigSpec.IntValue haAwarenessDuration;
    public final ForgeConfigSpec.IntValue haAwarenessCooldown;
    public final ForgeConfigSpec.BooleanValue haAwarenessEnabled;
    public final ForgeConfigSpec.IntValue haAwarenessRadius;
    public final ForgeConfigSpec.IntValue haPotionResistanceDuration;
    public final ForgeConfigSpec.IntValue haPotionResistanceCooldown;
    public final ForgeConfigSpec.BooleanValue haPotionResistanceEnabled;

    public final ForgeConfigSpec.DoubleValue hpStrengthMaxMod;
    public final ForgeConfigSpec.DoubleValue hpStrengthType;

    public final ForgeConfigSpec.DoubleValue hsSmallAttackSpeedModifier;
    public final ForgeConfigSpec.DoubleValue hsMajorAttackSpeedModifier;
    public final ForgeConfigSpec.DoubleValue hsSmallAttackDamageModifier;
    public final ForgeConfigSpec.BooleanValue hsInstantKill1FromBehind;
    public final ForgeConfigSpec.DoubleValue hsInstantKill1MaxHealth;
    public final ForgeConfigSpec.IntValue hsInstantKill2MaxHealth;
    public final ForgeConfigSpec.BooleanValue hsInstantKill2OnlyNPC;
    public final ForgeConfigSpec.IntValue hsGarlicDiffusorNormalDist;
    public final ForgeConfigSpec.IntValue hsGarlicDiffusorEnhancedDist;
    public final ForgeConfigSpec.IntValue hsGarlicDiffusorWeakDist;

    public final ForgeConfigSpec.BooleanValue viReplaceBlocks;
    public final ForgeConfigSpec.IntValue viPhase1Duration;
    public final ForgeConfigSpec.IntValue viNotifyDistanceSQ;
    public final ForgeConfigSpec.IntValue viForceTargetTime;
    public final ForgeConfigSpec.IntValue viTotemWeight;
    public final ForgeConfigSpec.IntValue viMaxVillagerRespawn;
    public final ForgeConfigSpec.IntValue viMaxTotemRadius;

    public final ForgeConfigSpec.DoubleValue vsSundamageReduction1;
    public final ForgeConfigSpec.DoubleValue vsBloodThirstReduction1;
    public final ForgeConfigSpec.DoubleValue vsSwordFinisherMaxHealth;
    public final ForgeConfigSpec.IntValue vsJumpBoost;
    public final ForgeConfigSpec.DoubleValue vsSpeedBoost;
    public final ForgeConfigSpec.IntValue vsBloodVisionDistanceSq;
    public final ForgeConfigSpec.DoubleValue vsSmallAttackDamageModifier;
    public final ForgeConfigSpec.DoubleValue vsSmallAttackSpeedModifier;

    public final ForgeConfigSpec.DoubleValue vpHealthMaxMod;
    public final ForgeConfigSpec.DoubleValue vpStrengthMaxMod;
    public final ForgeConfigSpec.DoubleValue vpResistanceMaxMod;
    public final ForgeConfigSpec.DoubleValue vpSpeedMaxMod;
    public final ForgeConfigSpec.DoubleValue vpExhaustionMaxMod;
    public final ForgeConfigSpec.DoubleValue vpBasicBloodExhaustionMod;
    public final ForgeConfigSpec.BooleanValue vpBloodUsagePeaceful;
    public final ForgeConfigSpec.DoubleValue vpPlayerBloodSaturation;
    public final ForgeConfigSpec.IntValue vpSanguinareAverageDuration;
    public final ForgeConfigSpec.IntValue vpSundamageMinLevel;
    public final ForgeConfigSpec.BooleanValue vpSundamageNausea;
    public final ForgeConfigSpec.IntValue vpSundamageNauseaMinLevel;
    public final ForgeConfigSpec.IntValue vpSundamageWeaknessMinLevel;
    public final ForgeConfigSpec.DoubleValue vpSundamage;
    public final ForgeConfigSpec.IntValue vpSundamageWaterblocks;
    public final ForgeConfigSpec.DoubleValue vpFireVulnerabilityMod;
    public final ForgeConfigSpec.BooleanValue vpFireResistanceReplace;
    public final ForgeConfigSpec.IntValue vpMaxYellowBorderPercentage;


    public final ForgeConfigSpec.IntValue vaFreezeCooldown;
    public final ForgeConfigSpec.BooleanValue vaFreezeEnabled;
    public final ForgeConfigSpec.IntValue vaFreezeDuration;
    public final ForgeConfigSpec.IntValue vaInvisibilityDuration;
    public final ForgeConfigSpec.IntValue vaInvisibilityCooldown;
    public final ForgeConfigSpec.BooleanValue vaInvisibilityEnabled;
    public final ForgeConfigSpec.IntValue vaRegenerationCooldown;
    public final ForgeConfigSpec.IntValue vaRegenerationDuration;
    public final ForgeConfigSpec.BooleanValue vaRegenerationEnabled;
    public final ForgeConfigSpec.IntValue vaTeleportMaxDistance;
    public final ForgeConfigSpec.IntValue vaTeleportCooldown;
    public final ForgeConfigSpec.BooleanValue vaTeleportEnabled;
    public final ForgeConfigSpec.IntValue vaRageCooldown;
    public final ForgeConfigSpec.IntValue vaRageMinDuration;
    public final ForgeConfigSpec.IntValue vaRageDurationIncrease;
    public final ForgeConfigSpec.BooleanValue vaRageEnabled;
    public final ForgeConfigSpec.IntValue vaSunscreenCooldown;
    public final ForgeConfigSpec.IntValue vaSunscreenDuration;
    public final ForgeConfigSpec.BooleanValue vaSunscreenEnabled;
    public final ForgeConfigSpec.IntValue vaBatCooldown;
    public final ForgeConfigSpec.IntValue vaBatDuration;
    public final ForgeConfigSpec.BooleanValue vaBatEnabled;
    public final ForgeConfigSpec.DoubleValue vaBatHealthReduction;
    public final ForgeConfigSpec.BooleanValue vaSummonBatsEnabled;
    public final ForgeConfigSpec.IntValue vaSummonBatsCooldown;
    public final ForgeConfigSpec.IntValue vaSummonBatsCount;
    public final ForgeConfigSpec.IntValue vaDisguiseDuration;
    public final ForgeConfigSpec.IntValue vaDisguiseCooldown;
    public final ForgeConfigSpec.BooleanValue vaDisguiseEnabled;
    public final ForgeConfigSpec.IntValue vaDarkBloodProjectileCooldown;
    public final ForgeConfigSpec.BooleanValue vaDarkBloodProjectileEnabled;
    public final ForgeConfigSpec.DoubleValue vaDarkBloodProjectileDamage;
    public final ForgeConfigSpec.IntValue vaHalfInvulnerableCooldown;
    public final ForgeConfigSpec.IntValue vaHalfInvulnerableDuration;
    public final ForgeConfigSpec.IntValue vaHalfInvulnerableBloodCost;
    public final ForgeConfigSpec.DoubleValue vaHalfInvulnerableThreshold;
    public final ForgeConfigSpec.BooleanValue vaHalfInvulnerableEnabled;

    public final ForgeConfigSpec.IntValue miResourceCooldown;
    public final ForgeConfigSpec.DoubleValue miResourceCooldownOfflineMult;
    public final ForgeConfigSpec.IntValue miDeathRecoveryTime;
    public final ForgeConfigSpec.IntValue miMinionPerLordLevel;

    public final ForgeConfigSpec.IntValue mbVampireSpawnChance;
    public final ForgeConfigSpec.IntValue mbAdvancedVampireSpawnChance;


    BalanceConfig(BalanceBuilder builder) {
        boolean iceAndFire = ModList.get().isLoaded("iceandfire");
        if (iceAndFire) {
            LOGGER.info("IceAndFire is loaded -> Adjusting default fire related configuration.");
        }

        //This is build using the intermediate builder to allow modification by addon mods.
        //It is finalized and assigned during RegistryEvent<Block>

        //GENERAL
        builder.comment("General options");
        builder.category("general", "");

        hunterTentDistance = builder.comment("Desired maximum distance in chunks between tents. Dont set hunterTentDistance <= hunterTentSeparation").defineInRange("hunterTentDistance", 10, 2, 4096);
        hunterTentSeparation = builder.comment("Desired minimum distance in chunks between tents. Dont set hunterTentDistance <= hunterTentSeparation").defineInRange("hunterTentSeparation", 4, 1, 4096);
        vampireForestWeight = builder.defineInRange("vampireForestWeight", 3, 1, Integer.MAX_VALUE);
        vampireForestHillsWeight = builder.defineInRange("vampireForestHillsWeight", 3, 1, Integer.MAX_VALUE);
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
        skillPointsPerLevel = builder.comment("Players receive n skill points for each leve-up. Anything except 1 is unbalanced.").defineInRange("skillPointsPerLevel", 1, 1, 20);
        allowInfiniteSpecialArrows = builder.comment("Whether special crossbow arrows (e.g. spitfire) can be used with infinity enchantment").define("allowInfiniteSpecialArrows", false);


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
        haAwarenessDuration = builder.comment("In ticks").defineInRange("awarenessDuration", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        haAwarenessCooldown = builder.comment("In ticks").defineInRange("awarenessCooldown", 1, 1, Integer.MAX_VALUE);
        haAwarenessRadius = builder.comment("Radius in which vampires should be detected").defineInRange("awarenessRadius", 18, 0, 50);
        haPotionResistanceEnabled = builder.define("potionResistanceEnabled", true);
        haPotionResistanceDuration = builder.comment("In ticks").defineInRange("potionResistanceDuration", 400, 1, Integer.MAX_VALUE);
        haPotionResistanceCooldown = builder.comment("In ticks").defineInRange("potionResistanceCooldown", 1200, 1, Integer.MAX_VALUE);

        //Hunter player
        builder.category("hunterPlayer", "hp");
        hpStrengthMaxMod = builder.comment("Strength = Old * (modifier+1").defineInRange("strengthMaxMod", 0.5, 0d, 4d);
        hpStrengthType = builder.comment("0.5 for square root, 1 for linear").defineInRange("strengthType", 0.5d, 0.5d, 1);

        //Hunter skills
        builder.category("hunterSkills", "hs");
        hsSmallAttackSpeedModifier = builder.comment("Basic skill - Weapon cooldown = 1/(oldvalue*(1+modifier))").defineInRange("smallAttackSpeedModifier", 0.2, 0, 3);
        hsMajorAttackSpeedModifier = builder.comment("Advanced skill - Weapon cooldown = 1/(oldvalue*(1+modifier)").defineInRange("majorAttackSpeedModifier", 0.4, 0, 3);
        hsSmallAttackDamageModifier = builder.comment("Increase damage - Attack damage = oldValue * (1+modifier)").defineInRange("smallAttackDamageModifier", 0.3d, 0, 2);
        hsInstantKill1FromBehind = builder.comment("First stake skill - If it is required to attack from behind to instant kill low level vampires").define("instantKill1FromBehind", false);
        hsInstantKill1MaxHealth = builder.comment("First stake skill -The maximal relative health a entity may have to be instantly killed").defineInRange("instantKill1MaxHealth", 0.35, 0, 1);
        hsInstantKill2MaxHealth = builder.comment("Second stake skill - The max (not the actual) health of an entity that can be one hit killed from behind").defineInRange("instantKill2MaxHealth", 200, 0, Integer.MAX_VALUE);
        hsInstantKill2OnlyNPC = builder.comment("Second stake skill - Whether only NPCs can be one hit killed with this skill").define("instantKill2OnlyNPC", false);
        hsGarlicDiffusorNormalDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorNormalDist", 0, 0, 5);
        hsGarlicDiffusorEnhancedDist = builder.comment("The chunk radius a enhanced diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorEnhancedDist", 1, 0, 5);
        hsGarlicDiffusorWeakDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorWeakDist", 2, 0, 5);

        //Village
        builder.category("village", "vi");
        viReplaceBlocks = builder.comment("Whether grass should slowly be replaced with cursed earth in vampire villages").define("replaceBlocks", true);
        viPhase1Duration = builder.comment("Duration of phase 1 of the capturing process in 2*seconds").defineInRange("phase1Duration", 80, 1, 1000);
        viNotifyDistanceSQ = builder.comment("Squared distance of village capture notification").defineInRange("notifyDistanceSQ", 40000, 0, 100000);
        viForceTargetTime = builder.comment("Time in 2*seconds in capture phase 2 after which the capture entities should find a target regardless of distance").defineInRange("forceTargetTime", 80, 1, 1000);
        viTotemWeight = builder.comment("Weight of the Totem Building inside the Village").defineInRange("totemWeight", 20, 1, Integer.MAX_VALUE);
        viMaxVillagerRespawn = builder.comment("Maximum of Villager the Totem can respawn").defineInRange("maxVillagerRespawn", 30, 0, Integer.MAX_VALUE);
        viMaxTotemRadius = builder.comment("Maximum range of a Totem to grow the village").defineInRange("maxTotemRadius", 100, 0, Integer.MAX_VALUE);


        //Vampire skills
        builder.category("vampireSkills", "vs");
        vsSundamageReduction1 = builder.comment("Sundamage is multipled with (value+1)").defineInRange("sundamageReduction1", -0.5, -1, 0);
        vsBloodThirstReduction1 = builder.comment("Blood exhaustion is multiplied with (value+1)").defineInRange("bloodThirstReduction1", -0.4, -1, 0);
        vsSwordFinisherMaxHealth = builder.comment("The max relative health for sword finisher kill").defineInRange("swordFinisherMaxHealth", 0.25, 0, 1);
        vsJumpBoost = builder.comment("Similar to potion effect ampliofier (and -1 is normal)").defineInRange("jumpBoost", 1, -1, 5);
        vsSpeedBoost = builder.comment("Max speed is multiplied with (value+1)").defineInRange("speedBoost", 0.15, 0, 3);
        vsBloodVisionDistanceSq = builder.comment("Squared blood vision distance").defineInRange("bloodVisionDistanceSq", 1600, 5, Integer.MAX_VALUE);
        vsSmallAttackDamageModifier = builder.comment("Damage = oldValue * (1+modifier)").defineInRange("smallAttackDamageModifier", 0.3d, 0, 2d);
        vsSmallAttackSpeedModifier = builder.comment("Basic skill - Weapon cooldown = 1/(oldvalue*(1+modifier))").defineInRange("smallAttackSpeedModifier", 0.2, 0, 3);


        //Vampire Player
        builder.category("vampirePlayer", "vp");
        vpHealthMaxMod = builder.defineInRange("healthMaxMod", 16, 0.5, 40);
        vpStrengthMaxMod = builder.defineInRange("strengthMaxMod", 0.25, 0, 2);
        vpResistanceMaxMod = builder.defineInRange("resistanceMaxMod", 4d, 0, 20);
        vpSpeedMaxMod = builder.defineInRange("speedMaxMod", 0.3, 0, 5);
        vpExhaustionMaxMod = builder.defineInRange("exhaustionMaxMod", 1.0, 0, 10);
        vpBasicBloodExhaustionMod = builder.comment("Blood exhaustion is multiplied with this value").defineInRange("basicBloodExhaustionMod", 0.7, 0, 5);
        vpBloodUsagePeaceful = builder.comment("Whether blood is consumed in peaceful gamemode").define("bloodUsagePeaceful", false);
        vpPlayerBloodSaturation = builder.defineInRange("playerBloodSaturation", 1.5, 0.3, 10);
        vpSanguinareAverageDuration = builder.defineInRange("sanguinareAverageDuration", 900, 1, 10000);
        vpSundamage = builder.defineInRange("sundamage", 7d, 1, Double.MAX_VALUE);
        vpSundamageMinLevel = builder.defineInRange("sundamageMinLevel", 4, 1, Integer.MAX_VALUE);
        vpSundamageNausea = builder.define("sundamageNausea", true);
        vpSundamageNauseaMinLevel = builder.defineInRange("sundamageNauseaMinLevel", 3, 1, Integer.MAX_VALUE);
        vpSundamageWeaknessMinLevel = builder.defineInRange("sundamageWeaknessMinLevel", 2, 1, Integer.MAX_VALUE);
        vpSundamageWaterblocks = builder.defineInRange("sundamageWaterblocks", 4, 1, 10);
        vpFireVulnerabilityMod = builder.comment("Multiply fire damage with this for vampires" + (iceAndFire ? " - Changed due to IceAndFire" : "")).defineInRange("fireVulnerabilityMod", iceAndFire ? 1.5d : 3d, 0.1, Double.MAX_VALUE);
        vpFireResistanceReplace = builder.comment("Whether to replace the vanilla fire resistance potion for vampires with a custom one that only reduces damage but does not remove it" + (iceAndFire ? " - Changed due to IceAndFire" : "")).define("fireResistanceReplace", !iceAndFire);
        vpMaxYellowBorderPercentage = builder.comment("Defines the maximum extend the yellow border covers when the player is in the sun. 100 is default. 0 to disable completely").defineInRange("maxYellowBorderPercentage", 100, 0, 100);

        //Vampire actions
        builder.category("vampireActions", "va");
        vaFreezeCooldown = builder.comment("In seconds").defineInRange("freezeCooldown", 60, 1, Integer.MAX_VALUE);
        vaFreezeDuration = builder.comment("In seconds").defineInRange("freezeDuration", 6, 1, 30);
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

        builder.category("mobs", "mb");
        mbVampireSpawnChance = builder.comment("Vampire spawn chance/weight (e.g. Zombie: 100)").defineInRange("vampireSpawnChance", 75, 0, 100000);
        mbAdvancedVampireSpawnChance = builder.comment("Advanceed vampire spawn chance/weight (e.g. Zombie: 100)").defineInRange("advancedVampireSpawnChance", 23, 0, 100000);

        builder.category("minions", "mi");
        miResourceCooldown = builder.comment("Cooldown in ticks,before new resources are added in collect resource task types").defineInRange("resourceCooldown", 1500, 20, Integer.MAX_VALUE);
        miResourceCooldownOfflineMult = builder.comment("Cooldown multiplier for collect resource task types while player is offline").defineInRange("resourceCooldownOfflineMult", 20D, 1D, 100000D);
        miDeathRecoveryTime = builder.comment("Time in seconds a minion needs to recover from death.").defineInRange("deathRecoveryTime", 180, 1, Integer.MAX_VALUE / 100);
        miMinionPerLordLevel = builder.comment("How many minions a player can have per lord level. Probably don't want to go very high").defineInRange("minionPerLordLevel", 1, 0, 100);

    }

}
