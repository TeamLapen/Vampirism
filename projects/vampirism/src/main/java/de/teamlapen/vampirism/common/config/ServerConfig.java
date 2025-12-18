package de.teamlapen.vampirism.common.config;

import de.teamlapen.factions.common.config.FactionConfig;
import de.teamlapen.vampirism.common.util.UtilLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

    public final ModConfigSpec.BooleanValue enforceRenderForestFog;
    public final ModConfigSpec.BooleanValue unlockAllSkills = FactionConfig.SERVER.unlockAllSkills;
    public final ModConfigSpec.IntValue sunscreenBeaconDistance;
    public final ModConfigSpec.BooleanValue sunscreenBeaconMineable;
    public final ModConfigSpec.BooleanValue autoCalculateEntityBlood;
    public final ModConfigSpec.BooleanValue playerCanTurnPlayer;
    public final ModConfigSpec.BooleanValue infectCreaturesSanguinare;
    public final ModConfigSpec.BooleanValue usePermissions = FactionConfig.SERVER.usePermissions;

    public final ModConfigSpec.BooleanValue sundamageUnknownDimension;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverridePositive;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverrideNegative;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDisabledBiomes;
    public final ModConfigSpec.ConfigValue<List<? extends String>> batDimensionBlacklist;


    public final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedBloodEntity;

    public final ModConfigSpec.BooleanValue disableFangInfection;
    public final ModConfigSpec.BooleanValue disableMobBiteInfection;

    public final ModConfigSpec.BooleanValue infoAboutGuideAPI;




    public ServerConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Server configuration settings")
                .push("server");
        enforceRenderForestFog = builder.comment("Prevent clients from disabling the vampire forest fog").define("enforceForestFog", true);
        sunscreenBeaconDistance = builder.comment("Block radius the sunscreen beacon affects").defineInRange("sunscreenBeaconDistance", 32, 1, 40000);
        sunscreenBeaconMineable = builder.comment("Whether the suncreen beacon can be mined in survival").define("sunscreenBeaconMineable", false);
        autoCalculateEntityBlood = builder.comment("Calculate the blood level for unknown creatures based on their size").define("autoCalculateEntityBlood", true);
        playerCanTurnPlayer = builder.comment("Whether players can infect other players").define("playersCanTurnPlayers", true);
        infectCreaturesSanguinare = builder.comment("If enabled, creatures are infected with Sanguinare Vampirism first instead of immediately being converted to a vampire when their blood is sucked dry").define("infectCreaturesSanguinare", false);
        batDimensionBlacklist = builder.comment("Prevent vampire players to transform into a bat").defineList("batDimensionBlacklist", Collections.singletonList(Level.END.location().toString()), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        builder.push("sundamage");
        sundamageUnknownDimension = builder.comment("Whether vampires should receive sundamage in unknown dimensions").define("sundamageUnknownDimension", false);
        sundamageDimensionsOverridePositive = builder.comment("Add the string id in quotes of any dimension (/vampirism currentDimension) you want to enforce sundamage for to this comma-separated list. Overrides defaults and values added by other mods").defineList("sundamageDimensionsOverridePositive", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        sundamageDimensionsOverrideNegative = builder.comment("Add the string id in quotes of any dimension (/vampirism currentDimension) you want to disable sundamage for to this comma-separated list. Overrides defaults and values added by other mods").defineList("sundamageDimensionsOverrideNegative", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        sundamageDisabledBiomes = builder.comment("Additional biomes the player should not get sundamage in. Use biome ids e.g. [\"minecraft:mesa\", \"minecraft:plains\"]").defineList("sundamageDisabledBiomes", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.BIOME, obj));
        builder.pop();
        builder.push("entities");
        blacklistedBloodEntity = builder.comment("Blacklist entities from predefined or auto calculated blood values").defineList("blacklistedBloodEntity", Collections.emptyList(), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.ENTITY_TYPE, obj));
        builder.pop();
        builder.push("cheats");
        builder.pop();
        builder.comment("Disabling these things might reduce fun or interfere with gameplay");
        builder.push("disable");
        disableFangInfection = builder.comment("Disable vampire fangs being usable to infect yourself").define("disableFangInfection", false);
        disableMobBiteInfection = builder.comment("Prevent vampire mobs from infecting players when attacking").define("disableMobBiteInfection", false);
        builder.pop();

        builder.push("internal");
        infoAboutGuideAPI = builder.comment("Send message about Guide-API once").define("infoAboutGuideAPI", true);
        builder.pop();
        builder.pop();
    }

}
