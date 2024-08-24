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

    public final ModConfigSpec.IntValue villageTotemWeight;
    public final ModConfigSpec.BooleanValue villageReplaceTemples;
    public final ModConfigSpec.IntValue villageHunterTrainerWeight;

    CommonConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Common configuration settings. Most other configuration can be found in the world (server)configuration folder")
                .push("common");
        this.collectStats = builder.comment("Send mod version, MC version and mod count to mod author").gameRestart().define("collectStats", true);

        builder.push("internal");
        this.integrationsNotifier = builder.comment("INTERNAL - Set to 'never' if you don't want to be notified about integration mods").define("integrationsNotifier", "");
        this.optifineBloodvisionWarning = builder.comment("INTERNAL").define("optifineBloodvisionWarning", false);
        builder.pop();
        builder.pop();

        builder.comment("Affects all worlds. This is only considered on server (or in singleplayer), but Forge requires us to put it here")
                .push("common-server");
        this.autoConvertGlassBottles = builder.comment("Whether glass bottles should be automatically be converted to blood bottles when needed").define("autoConvertGlassBottles", true);
        this.umbrella = builder.comment("If enabled adds a craftable umbrella that can be used to slowly walk though sunlight without taking damage").define("umbrella", false);
        this.enableFactionLogging = builder.comment("Enable a custom vampirism log file that logs specific faction actions").gameRestart().define("enableFactionLogging", false);

        builder.comment("Settings here require a game restart").push("world");
        this.addVampireForestToOverworld = builder.comment("Whether to inject the vampire forest into the default overworld generation and to replace some Taiga areas").gameRestart().define("addVampireForestToOverworld", true);
        this.vampireForestWeight_terrablender = builder.comment("Only considered if terrablender installed. Heigher values increase Vampirism region weight (likelyhood to appear)").gameRestart().defineInRange("vampireForestWeight_terrablender", 2, 1, 1000);
        this.enableHunterTentGeneration = builder.comment("Control hunter camp generation. If disabled you should set hunterSpawnChance to 75.").define("enableHunterTentGeneration", true);
        this.useVanillaCampfire = builder.comment("Use the vanilla campfire block instead of Vampirism's much cooler one").define("useVanillaCampfire", false);
        builder.push("village");
        villageTotemWeight = builder.comment("Weight of the Totem Building inside the Village").defineInRange("totemWeight", 20, 0, 140);
        villageHunterTrainerWeight = builder.comment("Weight of the Hunter Trainer Building inside the Village").defineInRange("villageHunterTrainerWeight", 50, 0, 140);
        villageReplaceTemples = builder.comment("Whether village Temples should be replaced with versions that contain church altars.").define("villageReplaceTemples", true);
        builder.pop();
        builder.pop();
    }

}
