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

        //


        builder.pop();

    }

}
