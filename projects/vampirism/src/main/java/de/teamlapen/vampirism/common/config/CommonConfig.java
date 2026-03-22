package de.teamlapen.vampirism.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public final ModConfigSpec.BooleanValue collectData;
    public final ModConfigSpec.BooleanValue autoConvertGlassBottles;
    public final ModConfigSpec.BooleanValue enableUmbrella;

    public final ModConfigSpec.BooleanValue addVampireForestToOverworld;
    public final ModConfigSpec.IntValue vampireForestWeightTerrablender;
    public final ModConfigSpec.BooleanValue generateHunterCamps;
    public final ModConfigSpec.BooleanValue useVanillaCampfire;

    public final ModConfigSpec.BooleanValue replaceVillageTemples;
    public final ModConfigSpec.IntValue villageTotemWeight;
    public final ModConfigSpec.IntValue villageHunterTrainerWeight;

    // Internal - Hidden
    public final ModConfigSpec.ConfigValue<String> notifyAvailableIntegrations;
    public final ModConfigSpec.BooleanValue optifineBloodVisionWarning;

    CommonConfig(ModConfigSpec.Builder builder) {
        this.collectData = builder
                .comment("Send the mod version, the MC version and the mod count to the mod authors. This allows us to identify the high-priority versions players play the most.")
                .gameRestart()
                .define("collectData", true);
        this.autoConvertGlassBottles = builder
                .comment("When enabled, automatically converts glass bottles to blood bottles when needed.")
                .define("autoConvertGlassBottles", true);
        this.enableUmbrella = builder
                .comment("When enabled, unlocks the crafting recipe of an umbrella that allows to slowly walk under sunlight without taking damage.")
                .define("enableUmbrella", false);

        builder.push("world");
        this.addVampireForestToOverworld = builder
                .comment("When enabled, injects the vampire forest into the default overworld generation, replacing some taiga areas.")
                .gameRestart()
                .define("addVampireForestToOverworld", true);
        this.vampireForestWeightTerrablender = builder
                .comment("Only considered if TerraBlender is installed. Higher values increase the Vampirism region weight, making it more likely to appear.")
                .gameRestart()
                .defineInRange("vampireForestWeightTerrablender", 4, 1, 1000);
        this.generateHunterCamps = builder
                .comment("When enabled, generates hunter camps in the world.")
                .define("generateHunterCamps", true);
        this.useVanillaCampfire = builder
                .comment("Set to true to use the vanilla campfire block in hunter camps instead of Vampirism's custom one.")
                .define("useVanillaCampfire", false);
        builder.push("village");
        this.villageTotemWeight = builder
                .comment("The weight of the totem building in village generation.")
                .defineInRange("villageTotemWeight", 20, 0, 140);
        this.villageHunterTrainerWeight = builder
                .comment("The weight of the hunter trainer building in village generation.")
                .defineInRange("villageHunterTrainerWeight", 50, 0, 140);
        this.replaceVillageTemples = builder
                .comment("When enabled, replaces village temples with versions that contain church altars from this mod.")
                .define("replaceVillageTemples", true);
        builder.pop();
        builder.pop();

        builder.push("internal");
        this.notifyAvailableIntegrations = builder
                .comment("INTERNAL - Set to 'never' to disable notifications about available integration mods.")
                .define("notifyAvailableIntegrations", "");
        this.optifineBloodVisionWarning = builder
                .comment("INTERNAL - Warns once if OptiFine is installed, as it breaks blood vision rendering.")
                .define("optifineBloodVisionWarning", false);
        builder.pop();
    }
}
