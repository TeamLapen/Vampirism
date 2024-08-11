package de.teamlapen.vampirism.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class CommonConfig {

    public final ModConfigSpec.BooleanValue collectStats;
    public final ModConfigSpec.BooleanValue enableFactionLogging;


    // Recipes server
    public final ModConfigSpec.BooleanValue autoConvertGlassBottles;
    public final ModConfigSpec.BooleanValue umbrella;

    // World generation
    public final ModConfigSpec.BooleanValue addVampireForestToOverworld;
    public final ModConfigSpec.IntValue vampireForestWeight_terrablender;
    public final ModConfigSpec.BooleanValue enableHunterTentGeneration;
    public final ModConfigSpec.BooleanValue useVanillaCampfire;

    // Internal - Hidden
    public final ModConfigSpec.ConfigValue<String> integrationsNotifier;
    public final ModConfigSpec.BooleanValue optifineBloodvisionWarning;

    CommonConfig(ModConfigSpec.@NotNull Builder builder) {
        this.collectStats = builder.comment("Send mod version, MC version and mod count to mod author").gameRestart().define("collectStats", true);
        this.enableFactionLogging = builder.comment("Enable a custom vampirism log file that logs specific faction actions").gameRestart().define("enableFactionLogging", false);

        builder.comment("Recipe modifications").push("recipes");
        this.autoConvertGlassBottles = builder.comment("Whether glass bottles should be automatically be converted to blood bottles when needed").define("autoConvertGlassBottles", true);
        this.umbrella = builder.comment("If enabled adds a craftable umbrella that can be used to slowly walk though sunlight without taking damage").define("umbrella", false);
        builder.pop();

        builder.comment("World generation").push("world-gen");
        this.addVampireForestToOverworld = builder.comment("Whether to inject the vampire forest into the default overworld generation and to replace some Taiga areas").gameRestart().define("addVampireForestToOverworld", true);
        this.vampireForestWeight_terrablender = builder.comment("Only considered if terrablender installed. Heigher values increase Vampirism region weight (likelyhood to appear)").gameRestart().defineInRange("vampireForestWeight_terrablender", 2, 1, 1000);
        this.enableHunterTentGeneration = builder.comment("Control hunter camp generation. If disabled you should set hunterSpawnChance to 75.").define("enableHunterTentGeneration", true);
        this.useVanillaCampfire = builder.comment("Use the vanilla campfire block instead of Vampirism's much cooler one").define("useVanillaCampfire", false);
        builder.pop();

        // Internal - Hidden
        this.integrationsNotifier = builder.comment("INTERNAL - Set to 'never' if you don't want to be notified about integration mods").define("integrationsNotifier", "");
        this.optifineBloodvisionWarning = builder.comment("INTERNAL").define("optifineBloodvisionWarning", false);
    }

}
