package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

    public final ModConfigSpec.BooleanValue enforceRenderForestFog;
    public final ModConfigSpec.BooleanValue unlockAllSkills;
    public final ModConfigSpec.BooleanValue pvpOnlyBetweenFactions;
    public final ModConfigSpec.BooleanValue pvpOnlyBetweenFactionsIncludeHumans;
    public final ModConfigSpec.IntValue sunscreenBeaconDistance;
    public final ModConfigSpec.BooleanValue sunscreenBeaconMineable;
    public final ModConfigSpec.BooleanValue autoCalculateEntityBlood;
    public final ModConfigSpec.BooleanValue playerCanTurnPlayer;
    public final ModConfigSpec.BooleanValue factionColorInChat;
    public final ModConfigSpec.BooleanValue lordPrefixInChat;
    public final ModConfigSpec.EnumValue<IMobOptions> entityIMob;
    public final ModConfigSpec.BooleanValue infectCreaturesSanguinare;
    public final ModConfigSpec.BooleanValue preventRenderingDebugBoundingBoxes;
    public final ModConfigSpec.BooleanValue allowVillageDestroyBlocks;
    public final ModConfigSpec.BooleanValue usePermissions;

    public final ModConfigSpec.BooleanValue sundamageUnknownDimension;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverridePositive;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverrideNegative;
    public final ModConfigSpec.ConfigValue<List<? extends String>> sundamageDisabledBiomes;
    public final ModConfigSpec.ConfigValue<List<? extends String>> batDimensionBlacklist;


    public final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedBloodEntity;

    public final ModConfigSpec.BooleanValue disableFangInfection;
    public final ModConfigSpec.BooleanValue disableMobBiteInfection;
    public final ModConfigSpec.BooleanValue disableVillageGuards;

    public final ModConfigSpec.BooleanValue infoAboutGuideAPI;




    ServerConfig(ModConfigSpec.@NotNull Builder builder) {
        builder.comment("Server configuration settings")
                .push("server");
        enforceRenderForestFog = builder.comment("Prevent clients from disabling the vampire forest fog").define("enforceForestFog", true);
        pvpOnlyBetweenFactions = builder.comment("If PVP should only be allowed between factions. PVP has to be enabled in the server properties for this. Not guaranteed to always protect player from teammates").define("pvpOnlyBetweenFactions", false);
        pvpOnlyBetweenFactionsIncludeHumans = builder.comment("If pvpOnlyBetweenFactions is enabled, this decides whether human players can be attacked and attack others").define("pvpOnlyBetweenFactionsIncludeHumans", false);
        sunscreenBeaconDistance = builder.comment("Block radius the sunscreen beacon affects").defineInRange("sunscreenBeaconDistance", 32, 1, 40000);
        sunscreenBeaconMineable = builder.comment("Whether the suncreen beacon can be mined in survival").define("sunscreenBeaconMineable", false);
        autoCalculateEntityBlood = builder.comment("Calculate the blood level for unknown creatures based on their size").define("autoCalculateEntityBlood", true);
        playerCanTurnPlayer = builder.comment("Whether players can infect other players").define("playersCanTurnPlayers", true);
        factionColorInChat = builder.comment("Whether to color player names in chat based on their current faction").define("factionColorInChat", true);
        lordPrefixInChat = builder.comment("Whether to add a prefix title based on the current lord level to the player names").define("lordPrefixInChat", true);
        entityIMob = builder.comment("Changes if entities are recognized as hostile by other mods. See https://github.com/TeamLapen/Vampirism/issues/199. Smart falls back to Never on servers ").defineEnum("entitiesIMob", IMobOptions.SMART);
        infectCreaturesSanguinare = builder.comment("If enabled, creatures are infected with Sanguinare Vampirism first instead of immediately being converted to a vampire when their blood is sucked dry").define("infectCreaturesSanguinare", false);
        preventRenderingDebugBoundingBoxes = builder.comment("Prevent players from enabling the rendering of debug bounding boxes. This can allow them to see certain entities they are not supposed to see (e.g. disguised hunter").define("preventDebugBoundingBoxes", false);
        batDimensionBlacklist = builder.comment("Prevent vampire players to transform into a bat").defineList("batDimensionBlacklist", Collections.singletonList(Level.END.location().toString()), () -> "", obj -> UtilLib.checkRegistryObjectExistence(Registries.DIMENSION, obj));
        allowVillageDestroyBlocks = builder.comment("Allow players to destroy point of interest blocks in faction villages if they no not have the faction village").define("allowVillageDestroyBlocks", false);
        usePermissions = builder.comment("Use the forge permission system for certain actions. Take a look at the wiki for more information").define("usePermissions", false);
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
        unlockAllSkills = builder.comment("If enabled, you will be able to unlock all skills at max level").define("allSkillsAtMaxLevel", false);
        builder.pop();
        builder.comment("Disabling these things might reduce fun or interfere with gameplay");
        builder.push("disable");
        disableFangInfection = builder.comment("Disable vampire fangs being usable to infect yourself").define("disableFangInfection", false);
        disableMobBiteInfection = builder.comment("Prevent vampire mobs from infecting players when attacking").define("disableMobBiteInfection", false);
        disableVillageGuards = builder.comment("Prevent villagers in hunter controlled villages to turn into guard villager when the village is attacked").define("disableVillageGuards", false);
        builder.pop();

        builder.push("internal");
        infoAboutGuideAPI = builder.comment("Send message about Guide-API once").define("infoAboutGuideAPI", true);
        builder.pop();
        builder.pop();
    }

    public enum IMobOptions {
        ALWAYS_IMOB, NEVER_IMOB, SMART
    }
}
