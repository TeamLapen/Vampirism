package de.teamlapen.vampirism.config;


import de.teamlapen.vampirism.api.VReference;
import net.minecraftforge.common.ForgeConfigSpec;

public class BalanceConfig {

    //GENERAL
    public final ForgeConfigSpec.IntValue vampireForestWeight;
    public final ForgeConfigSpec.BooleanValue canCancelSanguinare;
    public final ForgeConfigSpec.IntValue arrowVampireKillerMaxHealth;
    public final ForgeConfigSpec.IntValue holyWaterSplashDamage;
    public final ForgeConfigSpec.IntValue holyWaterNauseaDuration;
    public final ForgeConfigSpec.IntValue holyWaterBlindnessDuration;
    public final ForgeConfigSpec.IntValue dropOrchidFromLeavesChance;
    public final ForgeConfigSpec.DoubleValue holyWaterTierDamageInc;
    public final ForgeConfigSpec.DoubleValue heartSeekerChargingFactor;
    public final ForgeConfigSpec.DoubleValue heartSeekerUsageFactor;
    public final ForgeConfigSpec.BooleanValue golemAttackVampire;
    public final ForgeConfigSpec.BooleanValue zombieIgnoreVampire;
    public final ForgeConfigSpec.IntValue hunterTentMaxSpawn;

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

    public final ForgeConfigSpec.DoubleValue hpStrengthMaxMod;
    public final ForgeConfigSpec.IntValue hpStrengthLevelCap;
    public final ForgeConfigSpec.DoubleValue hpStrengthType;

    public final ForgeConfigSpec.DoubleValue hsSmallAttackSpeedModifier;
    public final ForgeConfigSpec.DoubleValue hsMajorAttackSpeedModifier;
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

    public final ForgeConfigSpec.DoubleValue vsSundamgeReduction1;
    public final ForgeConfigSpec.DoubleValue vsThirstReduction1;
    public final ForgeConfigSpec.DoubleValue vsBiteDamageMult;
    public final ForgeConfigSpec.DoubleValue vsSwordFinisherMaxHealth;
    public final ForgeConfigSpec.IntValue vsJumpBoost;
    public final ForgeConfigSpec.DoubleValue vsSpeedBoost;
    public final ForgeConfigSpec.IntValue vsBloodVisionDistSQ;
    public final ForgeConfigSpec.BooleanValue vsDisableAvoidedByCreepers;

    BalanceConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("A ton of options which allow you to balance the mod to your desire");
        builder.push("balance");


        //GENERAL
        builder.comment("General options");
        builder.push("general");

        vampireForestWeight = builder.defineInRange("vampireForestWeight", 7, 1, Integer.MAX_VALUE);
        canCancelSanguinare = builder.comment("If the sanguinare effect can be canceled by milk").define("canCancelSanguinare", true);
        arrowVampireKillerMaxHealth = builder.comment("The vampire killer arrow can only instant kill NPC vampires that have a max (not actual) health of this").defineInRange("arrowVampireKillerMaxHealth", 40, 1, Integer.MAX_VALUE);
        holyWaterSplashDamage = builder.comment("Damage a normal holy water splash bottle does when directly hitting a vampire").defineInRange("holyWaterSplashDamage", 5, 0, Integer.MAX_VALUE);
        holyWaterTierDamageInc = builder.comment("Holy water damage is multiplied with this value for each tier above normal").defineInRange("holyWaterTierDamageInc", 2d, 1d, 10d);
        holyWaterNauseaDuration = builder.comment("Duration of the nausea effect caused by enhanced or special holy water (ticks)").defineInRange("holyWaterNauseaDuration", 200, 0, 1000);
        holyWaterBlindnessDuration = builder.comment("Duration of the blindness effect caused by special holy water (ticks)").defineInRange("holyWaterBlindnessDuration", 160, 0, 1000);
        heartSeekerChargingFactor = builder.comment("The blood mB to charge percentage of the normal heart seeker vampire sword").defineInRange("heartSeekerChargingFactor", 0.05 / (double) VReference.FOOD_TO_FLUID_BLOOD, 0d, 1d);
        heartSeekerUsageFactor = builder.comment("The percentage of stored blood used for every hit with the normal heart seeker vampire sword").defineInRange("heartSeekerUsageFactor", 0.5, 0, 100d);
        dropOrchidFromLeavesChance = builder.comment("Drop orchid every n times breaking a leave in the vampire forest").defineInRange("dropOrchidFromLeavesChance", 25, 1, Integer.MAX_VALUE);
        golemAttackVampire = builder.comment("If iron golems should attack vampire NPCs if in a non-vampire village").define("golemAttackVampire", true);
        zombieIgnoreVampire = builder.comment("If zombies should ignore vampires").define("zombieIgnoreVampire", true);
        hunterTentMaxSpawn = builder.comment("Maximum number of hunters that can spawn at one tent per day").defineInRange("hunterTentMaxSpawn", 4, 0, 20);

        builder.pop();

        //Entity actions
        builder.push("entityActions");
        eaHealthThreshold = builder.comment("Relative health a entity must have to use actions").defineInRange("healthThreshold", 0.3, 0, 1);
        eaInvisibilityCooldown = builder.comment("In seconds").defineInRange("invisibilityCooldown", 7, 1, Integer.MAX_VALUE);
        eaInvisibilityDuration = builder.comment("In seconds").defineInRange("invisibilityDuration", 4, 1, Integer.MAX_VALUE);
        eaHealAmount = builder.comment("In percent").defineInRange("healAmount", 30, 0, 100);
        eaHealCooldown = builder.comment("In seconds").defineInRange("healCooldown", 7, 1, Integer.MAX_VALUE);
        eaRegenerationDuration = builder.comment("In seconds").defineInRange("regenerationDuration", 5, 0, Integer.MAX_VALUE);
        eaRegenerationAmount = builder.comment("In percent").defineInRange("regenerationAmount", 40, 0, 100);
        eaRegenerationCooldown = builder.comment("In seconds").defineInRange("regenerationCooldown", 8, 0, Integer.MAX_VALUE);
        eaSpeedDuration = builder.comment("In seconds").defineInRange("speedDuration", 4, 0, Integer.MAX_VALUE);
        eaSpeedCooldown = builder.comment("In seconds").defineInRange("speedCooldonw", 6, 1, Integer.MAX_VALUE);
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
        builder.pop();

        //Hunter actions
        builder.push("hunterActions");
        haDisguiseEnabled = builder.define("disguiseEnabled", true);
        haDisguiseVisibilityMod = builder.comment("If disguised the detection radius of mobs will be multiplied by this").defineInRange("disguiseInvisibilityMod", 0.1D, 0, 1);
        haDisguiseInvisibleSQ = builder.comment("Squared distance as of which a disguised hunter is invisible").defineInRange("disguiseInvisibileSQ", 256, 1, Integer.MAX_VALUE);
        haAwarenessEnabled = builder.define("awarenessEnabled", true);
        haAwarenessDuration = builder.comment("In ticks").defineInRange("awarenessDuration", 2400, 1, Integer.MAX_VALUE);
        haAwarenessCooldown = builder.comment("In ticks").defineInRange("awarenessCooldown", 1200, 1, Integer.MAX_VALUE);
        haAwarenessRadius = builder.comment("Radius in which vampires should be detected").defineInRange("awarenessRadius", 24, 0, 50);
        builder.pop();

        //Hunter player
        builder.push("hunterPlayer");
        hpStrengthLevelCap = builder.defineInRange("strengthLevelCap", 20, 10, 40);
        hpStrengthMaxMod = builder.comment("Stringth = Old * (modifier+1").defineInRange("strengthMaxMod", 2d, 0.5d, 4d);
        hpStrengthType = builder.comment("0.5 for square root, 1 for linear").defineInRange("strengthType", 0.5d, 0.5d, 1);
        builder.pop();

        //Hunter skills
        builder.push("hunterSkills");
        hsSmallAttackSpeedModifier = builder.comment("Basic skill - Weapon cooldown = 1/(oldvalue*(1+modifier))").defineInRange("smallAttackSpeedModifier", 0.2, 0, 3);
        hsMajorAttackSpeedModifier = builder.comment("Advanced skill - Weapon cooldown = 1/(oldvalue*(1+modifier)").defineInRange("majorAttackSpeedModifier", 0.4, 0, 3);
        hsInstantKill1FromBehind = builder.comment("First stake skill - If it is required to attack from behind to instant kill low level vampires").define("instantKill1FromBehind", false);
        hsInstantKill1MaxHealth = builder.comment("First stake skill -The maximal relative health a entity may have to be instantly killed").defineInRange("instantKill1MaxHealth", 0.35, 0, 1);
        hsInstantKill2MaxHealth = builder.comment("Second stake skill - The max (not the actual) health of an entity that can be one hit killed from behind").defineInRange("instantKill2MaxHealth", 200, 0, Integer.MAX_VALUE);
        hsInstantKill2OnlyNPC = builder.comment("Second stake skill - Whether only NPCs can be one hit killed with this skill").define("instantKill2OnlyNPC", false);
        hsGarlicDiffusorNormalDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorNormalDist", 0, 0, 5);
        hsGarlicDiffusorEnhancedDist = builder.comment("The chunk radius a enhanced diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorEnhancedDist", 1, 0, 5);
        hsGarlicDiffusorWeakDist = builder.comment("The chunk radius a normal diffusor affects. 0 results in a one chunk area. Changing this only affects newly placed blocks").defineInRange("garlicDiffusorWeakDist", 2, 0, 5);
        builder.pop();

        //Village
        builder.push("village");
        viReplaceBlocks = builder.comment("Whether grass should slowly be replaced with cursed earth in vampire villages").define("replaceBlocks", true);
        viPhase1Duration = builder.comment("Duration of phase 1 of the capturing process in 2*seconds").defineInRange("phase1Duration", 80, 1, 1000);
        viNotifyDistanceSQ = builder.comment("Squared distance of village capture notification").defineInRange("notifyDistanceSQ", 40000, 0, 100000);
        viForceTargetTime = builder.comment("Time in 2*seconds in capture phase 2 after which the capture entities should find a target regardless of distance").defineInRange("forceTargetTime", 80, 1, 1000);
        builder.pop();


        //Vampire skills
        builder.push("vampireSkills");
        vsSundamgeReduction1 = builder.comment("Sundamage is multipled with (value+1)").defineInRange("sundamageReduction1", -0.3, -1, 0);
        vsThirstReduction1 = builder.comment("Blood exhaustion is multiplied with (value+1)").defineInRange("bloodThirstReduction1", -0.4, -1, 0);
        vsBiteDamageMult = builder.comment("Bite damage is multiplied with (value+1)").defineInRange("biteDamageMult", 1d, 0, 100);
        vsSwordFinisherMaxHealth = builder.comment("The max relative health for sword finisher kill").defineInRange("swordFinisherMaxHealth", 0.25, 0, 1);
        vsJumpBoost = builder.comment("Similar to potion effect ampliofier (and -1 is normal)").defineInRange("jumpBoost", 1, -1, 5);
        vsSpeedBoost = builder.comment("Max speed is multiplied with (value+1)").defineInRange("speedBoost", 0.15, 0, 3);
        vsBloodVisionDistSQ = builder.comment("Squared blood vision distance").defineInRange("bloodVisionDistanceSq", 1600, 5, Integer.MAX_VALUE);
        vsDisableAvoidedByCreepers = builder.comment("Disables the effect of 'Avoided by creepers'. Can still be unlocked though.").define("disableAvoidedByCreepers", false);
        builder.pop();


        //


        builder.pop();

    }

}
