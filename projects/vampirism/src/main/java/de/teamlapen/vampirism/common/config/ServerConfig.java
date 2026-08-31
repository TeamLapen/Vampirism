package de.teamlapen.vampirism.common.config;

import de.teamlapen.vampirism.common.util.UtilLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

    public final ModConfigSpec.BooleanValue enforceRenderForestFog;
    public final ModConfigSpec.BooleanValue playerCanInfectPlayers;
    public final ModConfigSpec.BooleanValue fangInfection;
    public final ModConfigSpec.BooleanValue mobBiteInfection;

    public final ModConfigSpec.BooleanValue autoCalculateEntityBlood;
    public final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedBloodEntities;

    public final ModConfigSpec.IntValue sunscreenBeaconRadius;
    public final ModConfigSpec.BooleanValue sunscreenBeaconMineable;
    public final ModConfigSpec.ConfigValue<List<? extends String>> batDimensionBlacklist;

    public final ModConfigSpec.BooleanValue sundamageInUnknownDimensions;
    public final ModConfigSpec.ConfigValue<List<? extends String>> enforceSundamageDimensions;
    public final ModConfigSpec.ConfigValue<List<? extends String>> noSundamageDimensions;
    public final ModConfigSpec.ConfigValue<List<? extends String>> noSundamageBiomes;

    public final ModConfigSpec.BooleanValue informAboutGuideAPI;

    public ServerConfig(ModConfigSpec.Builder builder) {
        this.enforceRenderForestFog = builder
                .comment("When enabled, prevents clients from disabling the vampire forest fog.")
                .define("enforceRenderForestFog", true);
        this.playerCanInfectPlayers = builder
                .comment("When enabled, players can infect other players with sanguinare.")
                .define("playerCanInfectPlayers", true);
        this.fangInfection = builder
                .comment("Set to false to prevent vampire fangs from being usable to infect oneself.")
                .define("fangInfection", true);
        this.mobBiteInfection = builder
                .comment("Set to false to prevent vampire mobs from infecting players on attack.")
                .define("mobBiteInfection", true);
        this.batDimensionBlacklist = builder
                .comment("A list of dimensions where vampire players cannot transform into a bat. Use dimension IDs e.g. [\"minecraft:the_end\"].")
                .defineList("batDimensionBlacklist", Collections.singletonList(Level.END.identifier().toString()), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));

        builder.push("entities");
        this.autoCalculateEntityBlood = builder
                .comment("When enabled, calculates blood levels for creatures without pre-written ones based on their size.")
                .define("autoCalculateEntityBlood", true);
        this.blacklistedBloodEntities = builder
                .comment("List of entities excluded from predefined or auto-calculated blood values. Use entity type IDs e.g. [\"minecraft:cow\"].")
                .defineList("blacklistedBloodEntities", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.ENTITY_TYPE, obj));
        this.sunscreenBeaconRadius = builder
                .comment("The radius the sunscreen beacon protects against sun damage.")
                .defineInRange("sunscreenBeaconRadius", 32, 1, 40000);
        this.sunscreenBeaconMineable = builder
                .comment("When enabled, the sunscreen beacon can be mined in survival mode.")
                .define("sunscreenBeaconMineable", false);
        builder.pop();

        builder.push("sundamage");
        this.sundamageInUnknownDimensions = builder
                .comment("When enabled, vampires receive sun damage in dimensions not explicitly configured.")
                .define("sundamageInUnknownDimensions", false);
        this.enforceSundamageDimensions = builder
                .comment("Dimensions to always apply sun damage in, regardless of other settings. Use dimension IDs from /vampirism currentDimension.")
                .defineList("enforceSundamageDimensions", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        this.noSundamageDimensions = builder
                .comment("Dimensions to never apply sun damage in, regardless of other settings. Use dimension IDs from /vampirism currentDimension.")
                .defineList("noSundamageDimensions", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        this.noSundamageBiomes = builder
                .comment("Additional biomes where vampires do not receive sun damage. Use biome IDs e.g. [\"minecraft:mesa\", \"minecraft:plains\"].")
                .defineList("noSundamageBiomes", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.BIOME, obj));
        builder.pop();

        builder.push("internal");
        this.informAboutGuideAPI = builder
                .comment("INTERNAL - When enabled, sends a one-time message about the Guide-API integration.")
                .define("informAboutGuideAPI", true);
        builder.pop();
    }
}
