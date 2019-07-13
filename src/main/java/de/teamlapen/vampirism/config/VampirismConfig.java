package de.teamlapen.vampirism.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.loading.LogMarkers.FORGEMOD;

public class VampirismConfig {

    public static final Client CLIENT;
    public static final Server SERVER;
    public static final Common COMMON;
    private static final ForgeConfigSpec clientSpec;
    private static final ForgeConfigSpec serverSpec;
    private static final ForgeConfigSpec commonSpec;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        FMLJavaModLoadingContext.get().getModEventBus().register(VampirismConfig.class);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        LogManager.getLogger().debug(FORGEMOD, "Loaded forge config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        LogManager.getLogger().fatal(CORE, "Forge config just got changed on the file system!");
    }

    /**
     * This is stored server side on an per world base.
     * Config is synced to clients on connect
     */
    public static class Server {

        public final ForgeConfigSpec.BooleanValue enforceRenderForestFog;
        public final ForgeConfigSpec.IntValue coffinSleepPercentage;
        public final ForgeConfigSpec.BooleanValue batModeInEnd;
        public final ForgeConfigSpec.BooleanValue unlockAllSkills;
        public final ForgeConfigSpec.BooleanValue pvpOnlyBetweenFactions;
        public final ForgeConfigSpec.IntValue sunscreenBeaconDistance;
        public final ForgeConfigSpec.BooleanValue sunscreenBeaconMineable;
        public final ForgeConfigSpec.BooleanValue autoCalculateEntityBlood;
        public final ForgeConfigSpec.BooleanValue autoConvertGlassBottles;
        public final ForgeConfigSpec.BooleanValue playerCanTurnPlayer;
        public final ForgeConfigSpec.BooleanValue factionColorInChat;

        public final ForgeConfigSpec.IntValue villageDistance;
        public final ForgeConfigSpec.IntValue villageSeparation;
        public final ForgeConfigSpec.BooleanValue villageModify;

        public final ForgeConfigSpec.BooleanValue disableVampireForest;
        public final ForgeConfigSpec.BooleanValue disableFangInfection;
        public final ForgeConfigSpec.BooleanValue disableMobBiteInfection;
        public final ForgeConfigSpec.BooleanValue disableHunterCamps;
        public final ForgeConfigSpec.BooleanValue disableHalloweenSpecial;


        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");

            enforceRenderForestFog = builder.comment("Prevent clients from disabling the vampire forest fog").define("enforceForestFog", true);
            coffinSleepPercentage = builder.comment("Percentage of players that have to lay in a coffin to make it night.  Be careful with values below 51 if e.g. Morpheus is installed").defineInRange("coffinSleepPercentage", 100, 1, 100);
            batModeInEnd = builder.comment("If vampires can convert to a bat in the End").define("batModeInEnd", false);
            pvpOnlyBetweenFactions = builder.comment("If PVP should only be allowed between factions. PVP has to be enabled in the server properties for this. Not guaranteed to always protect player from teammates").define("pvpOnlyBetweenFactions", false);
            sunscreenBeaconDistance = builder.comment("Block radius the sunscreen beacon affects").defineInRange("sunscreenBeaconDistance", 32, 1, 40000);
            sunscreenBeaconMineable = builder.comment("Whether the suncreen beacon can be mined in survival").define("sunscreenBeaconMineable", false);
            autoCalculateEntityBlood = builder.comment("Calculate the blood level for unknown creatures based on their size").define("autoCalculateEntityBlood", true);
            autoConvertGlassBottles = builder.comment("Whether glass bottles should be automatically be converted to blood bottles when needed").define("autoConvertGlassBottles", true);
            playerCanTurnPlayer = builder.comment("Whether players can infect other players").define("playersCanTurnPlayers", true);
            factionColorInChat = builder.comment("Whether to color player names in chat based on their current faction").define("factionColorInChat", true);

            builder.push("village");
            villageModify = builder.comment("Whether to modify the village world gen (size and frequency)").define("villageModify", true);
            villageDistance = builder.comment("Village distance").defineInRange("villageDistance", 32, 1, 100); //TODO 1.14 improve comment
            villageSeparation = builder.comment("Village centers will be at least N chunks apart. Must be smaller than distance").defineInRange("villageSeparation", 8, 1, 100);
            builder.pop();

            builder.push("cheats");
            unlockAllSkills = builder.comment("CHEAT: If enabled, you will be able to unlock all skills at max level").define("allSkillsAtMaxLevel", false);
            builder.pop();

            builder.comment("Disabling these things might reduce fun or interfere with gameplay");
            builder.push("disable");
            disableFangInfection = builder.comment("Disable vampire fangs being usable to infect yourself").define("disableFangInfection", false);
            disableHalloweenSpecial = builder.comment("Disable Halloween special event").define("disableHalloweenSpecialEvent", false);
            disableHunterCamps = builder.comment("Disable the generation of hunter camps completely").define("disableHunterCamps", false);
            disableMobBiteInfection = builder.comment("Prevent vampire mobs from infecting players when attacking").define("disableMobBiteInfection", false);
            disableVampireForest = builder.comment("Disable vampire forest generation").define("disableVampireForest", false);

            builder.pop();

            builder.pop();
        }
    }

    /**
     * Client only configuration
     */
    public static class Client {

        public final ForgeConfigSpec.IntValue guiLevelOffsetX;
        public final ForgeConfigSpec.IntValue guiLevelOffsetY;
        public final ForgeConfigSpec.BooleanValue guiSkillButton;
        public final ForgeConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
        public final ForgeConfigSpec.BooleanValue renderVampireEyes;
        public final ForgeConfigSpec.BooleanValue renderVampireForestFog;
        public final ForgeConfigSpec.BooleanValue renderScreenOverlay;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client configuration settings")
                    .push("client");


            //Rendering
            builder.comment("Configure rendering").push("render");
            renderAdvancedMobPlayerFaces = builder.comment("Render player faces on advanced hunter or vampires").define("advancedMobPlayerFaces", true);
            renderVampireEyes = builder.comment("Render vampire eye/fang face overlay").define("vampireEyes", true);
            renderVampireForestFog = builder.comment("Render fog in vampire biome. Might be enforced server side").define("vampireForestFog", true);
            renderScreenOverlay = builder.comment("Render screen overlay. Don't disable").define("screenOverlay", true);

            builder.pop();

            builder.comment("Configure GUI").push("gui");
            guiLevelOffsetX = builder.comment("X-Offset of the level indicator from the center in pixels").defineInRange("levelOffsetX", 0, -250, 250);
            guiLevelOffsetY = builder.comment("Y-Offset of the level indicator from the bottom in pixels").defineInRange("levelOffsetY", 47, 0, 270);
            guiSkillButton = builder.comment("Render skill menu button in inventory").define("skillButtonEnable", true);
            builder.pop();

            builder.pop();
        }
    }

    /**
     * Separate configuration for client and server.
     * E.g. enable update check
     * Not synced
     */
    public static class Common {

        public final ForgeConfigSpec.BooleanValue versionCheck;
        public final ForgeConfigSpec.BooleanValue collectStats;


        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings")
                    .push("common");
            versionCheck = builder.comment("Check for new versions of Vampirism on startup").define("versionCheck", true);
            collectStats = builder.comment("Send mod version, MC version and mod count to mod author").define("collectStats", true);

            builder.pop();
        }
    }
}
