package de.teamlapen.vampirism.config;


import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class VampirismConfig {

    /**
     * For client side only configuration.
     * Loaded after registry events but before setup
     */
    public static final Client CLIENT;
    /**
     * Synced to clients.
     * Only loaded on world load
     */
    public static final Server SERVER;
    /**
     * For side independent configuration. Not synced.
     * Loaded after registry events but before setup
     */
    public static final Common COMMON;

    public static final @NotNull BalanceConfig BALANCE;
    private static final ForgeConfigSpec clientSpec;
    private static final ForgeConfigSpec serverSpec;
    private static final ForgeConfigSpec commonSpec;
    private static @Nullable BalanceBuilder balanceBuilder;

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

    static {
        balanceBuilder = new BalanceBuilder();
        BALANCE = new BalanceConfig(balanceBuilder);
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {

    }

    @ThreadSafeAPI
    public static <T extends BalanceBuilder.Conf> void addBalanceModification(@NotNull String key, @NotNull Consumer<T> modifier) {
        if (balanceBuilder == null) {
            throw new IllegalStateException("Must add balance modifications during mod construction");
        }
        balanceBuilder.addBalanceModifier(key, modifier);
    }

    public static void finalizeAndRegisterConfig() {
        if (balanceBuilder == null) return;
        /*
        Build balance configuration
         */
        final Pair<BalanceConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure((builder) -> {
            builder.comment("A ton of options which allow you to balance the mod to your desire");
            builder.push("balance");
            balanceBuilder.build(BALANCE, builder);
            builder.pop();
            return BALANCE;
        });
        ForgeConfigSpec balanceSpec = specPair.getRight();
        if (VampirismMod.inDev) {
            balanceBuilder.checkFields(BALANCE);
        }
        balanceBuilder = null;


        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, balanceSpec, "vampirism-balance.toml");
        FMLJavaModLoadingContext.get().getModEventBus().register(VampirismConfig.class);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.@NotNull Loading configEvent) {
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).reloadConfiguration();

        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.@NotNull Reloading configEvent) {
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).reloadConfiguration();
        }
    }

    /**
     * This is stored server side on a per world base.
     * Config is synced to clients on connect
     */
    public static class Server {

        public final ForgeConfigSpec.BooleanValue enforceRenderForestFog;
        @Deprecated
        public final ForgeConfigSpec.BooleanValue batModeInEnd;
        public final ForgeConfigSpec.BooleanValue unlockAllSkills;
        public final ForgeConfigSpec.BooleanValue pvpOnlyBetweenFactions;
        public final ForgeConfigSpec.BooleanValue pvpOnlyBetweenFactionsIncludeHumans;
        public final ForgeConfigSpec.IntValue sunscreenBeaconDistance;
        public final ForgeConfigSpec.BooleanValue sunscreenBeaconMineable;
        public final ForgeConfigSpec.BooleanValue autoCalculateEntityBlood;
        public final ForgeConfigSpec.BooleanValue playerCanTurnPlayer;
        public final ForgeConfigSpec.BooleanValue factionColorInChat;
        public final ForgeConfigSpec.BooleanValue lordPrefixInChat;
        public final ForgeConfigSpec.EnumValue<IMobOptions> entityIMob;
        public final ForgeConfigSpec.BooleanValue infectCreaturesSanguinare;
        public final ForgeConfigSpec.BooleanValue preventRenderingDebugBoundingBoxes;

        public final ForgeConfigSpec.BooleanValue sundamageUnknownDimension;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverridePositive;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> sundamageDimensionsOverrideNegative;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> sundamageDisabledBiomes;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> batDimensionBlacklist;


        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedBloodEntity;

        public final ForgeConfigSpec.BooleanValue disableFangInfection;
        public final ForgeConfigSpec.BooleanValue disableMobBiteInfection;
        public final ForgeConfigSpec.BooleanValue disableVillageGuards;

        public final ForgeConfigSpec.BooleanValue oldVampireBiomeGen;

        public final ForgeConfigSpec.BooleanValue infoAboutGuideAPI;


        Server(ForgeConfigSpec.@NotNull Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");

            enforceRenderForestFog = builder.comment("Prevent clients from disabling the vampire forest fog").define("enforceForestFog", true);
            batModeInEnd = builder.comment("Deprecated! Use 'batDimensionBlacklist'", "If vampires can convert to a bat in the End").define("batModeInEnd", false);
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
            batDimensionBlacklist = builder.comment("Prevent vampire players to transform into a bat").defineList("batDimensionBlacklist", Collections.singletonList(Level.END.location().toString()), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));

            builder.push("sundamage");
            sundamageUnknownDimension = builder.comment("Whether vampires should receive sundamage in unknown dimensions").define("sundamageUnknownDimension", false);
            sundamageDimensionsOverridePositive = builder.comment("Add the string id in quotes of any dimension (/vampirism currentDimension) you want to enforce sundamage for to this comma-separated list. Overrides defaults and values added by other mods").defineList("sundamageDimensionsOverridePositive", Collections.emptyList(), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));

            sundamageDimensionsOverrideNegative = builder.comment("Add the string id in quotes of any dimension (/vampirism currentDimension) you want to disable sundamage for to this comma-separated list. Overrides defaults and values added by other mods").defineList("sundamageDimensionsOverrideNegative", Collections.emptyList(), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));
            sundamageDisabledBiomes = builder.comment("Additional biomes the player should not get sundamage in. Use biome ids e.g. [\"minecraft:mesa\", \"minecraft:plains\"]").defineList("sundamageDisabledBiomes", Collections.emptyList(), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));
            builder.pop();

            builder.push("entities");
            blacklistedBloodEntity = builder.comment("Blacklist entities from predefined or auto calculated blood values").defineList("blacklistedBloodEntity", Collections.emptyList(), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));
            builder.pop();

            builder.push("cheats");
            unlockAllSkills = builder.comment("CHEAT: If enabled, you will be able to unlock all skills at max level").define("allSkillsAtMaxLevel", false);
            builder.pop();

            builder.comment("Disabling these things might reduce fun or interfere with gameplay");
            builder.push("disable");
            disableFangInfection = builder.comment("Disable vampire fangs being usable to infect yourself").define("disableFangInfection", false);
            disableMobBiteInfection = builder.comment("Prevent vampire mobs from infecting players when attacking").define("disableMobBiteInfection", false);
            disableVillageGuards = builder.comment("Prevent villagers in hunter controlled villages to turn into guard villager when the village is attacked").define("disableVillageGuards", false);
            builder.pop();

            builder.push("internal");
            infoAboutGuideAPI = builder.comment("Send message about Guide-API once").define("infoAboutGuideAPI", true);
            oldVampireBiomeGen = builder.comment("If world was generated using the old vampirism biome").define("oldVampireBiomeGen", true);
            builder.pop();

            builder.pop();
        }

        public enum IMobOptions {
            ALWAYS_IMOB, NEVER_IMOB, SMART
        }
    }

    /**
     * Client only configuration
     */
    public static class Client {

        public final ForgeConfigSpec.IntValue overrideGuiSkillButtonX;
        public final ForgeConfigSpec.IntValue overrideGuiSkillButtonY;
        public final ForgeConfigSpec.IntValue guiLevelOffsetX;
        public final ForgeConfigSpec.IntValue guiLevelOffsetY;
        public final ForgeConfigSpec.BooleanValue guiSkillButton;
        public final ForgeConfigSpec.BooleanValue renderAdvancedMobPlayerFaces;
        public final ForgeConfigSpec.BooleanValue renderVampireEyes;
        public final ForgeConfigSpec.BooleanValue renderVampireForestFog;
        public final ForgeConfigSpec.BooleanValue renderScreenOverlay;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> actionOrder;
        public final ForgeConfigSpec.BooleanValue disableFovChange;
        public final ForgeConfigSpec.BooleanValue disableBloodVisionRendering;
        public final ForgeConfigSpec.BooleanValue disableHudActionCooldownRendering;
        public final ForgeConfigSpec.BooleanValue disableHudActionDurationRendering;

        Client(ForgeConfigSpec.@NotNull Builder builder) {
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
            overrideGuiSkillButtonX = builder.comment("Force the guiSkillButton to the following x position from the center of the inventory, default value is 125").defineInRange("overrideGuiSkillButtonX", 125, Integer.MIN_VALUE, Integer.MAX_VALUE);
            overrideGuiSkillButtonY = builder.comment("Force the guiSkillButton to the following y position from the center of the inventory, default value is -22").defineInRange("overrideGuiSkillButtonY", -22, Integer.MIN_VALUE, Integer.MAX_VALUE);

            actionOrder = builder.comment("Action Order in Select Action Screen (reset with \"\"), unnamed actions will appended").defineList("actionOrder", Collections.emptyList(), string -> string instanceof String && UtilLib.isValidResourceLocation(((String) string)));
            disableFovChange = builder.comment("Disable the FOV change caused by the speed buf for vampire players").define("disableFovChange", false);
            disableBloodVisionRendering = builder.comment("Disable the effect of blood vision. It can still be unlocked and activated but does not have any effect").define("disableBloodVisionRendering", false);
            disableHudActionCooldownRendering = builder.comment("Disable the rendering of the action cooldowns in the HUD").define("disableHudActionCooldownRendering", false);
            disableHudActionDurationRendering = builder.comment("Disable the rendering of the action durations in the HUD").define("disableHudActionDurationRendering", false);

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
        public final ForgeConfigSpec.ConfigValue<String> integrationsNotifier;
        public final ForgeConfigSpec.BooleanValue optifineBloodvisionWarning;

        //Common server
        public final ForgeConfigSpec.BooleanValue autoConvertGlassBottles;
        public final ForgeConfigSpec.BooleanValue umbrella;

        //World
        public final ForgeConfigSpec.BooleanValue addVampireForestToOverworld;
        public final ForgeConfigSpec.IntValue vampireForestWeight_terrablender;
        public final ForgeConfigSpec.BooleanValue enableHunterTentGeneration;
        public final ForgeConfigSpec.BooleanValue useVanillaCampfire;

        //World village
        public final ForgeConfigSpec.IntValue villageTotemWeight;
        public final ForgeConfigSpec.BooleanValue villageReplaceTemples;
        public final ForgeConfigSpec.DoubleValue villageTotemFactionChance;
        public final ForgeConfigSpec.IntValue villageHunterTrainerWeight;


        Common(ForgeConfigSpec.@NotNull Builder builder) {
            builder.comment("Common configuration settings. Most other configuration can be found in the world (server)configuration folder")
                    .push("common");
            versionCheck = builder.comment("Check for new versions of Vampirism on startup").define("versionCheck", true);
            collectStats = builder.comment("Send mod version, MC version and mod count to mod author").define("collectStats", true);

            builder.push("internal");
            integrationsNotifier = builder.comment("INTERNAL - Set to 'never' if you don't want to be notified about integration mods").define("integrationsNotifier", "");
            optifineBloodvisionWarning = builder.comment("INTERNAL").define("optifineBloodvisionWarning", false);
            builder.pop();

            builder.pop();
            builder.comment("Affects all worlds. This is only considered on server (or in singleplayer), but Forge requires us to put it here")
                    .push("common-server");
            autoConvertGlassBottles = builder.comment("Whether glass bottles should be automatically be converted to blood bottles when needed").define("autoConvertGlassBottles", true);
            umbrella = builder.comment("If enabled adds a craftable umbrella that can be used to slowly walk though sunlight without taking damage").define("umbrella", false);

            builder.comment("Settings here require a game restart").push("world");
            addVampireForestToOverworld = builder.comment("Whether to inject the vampire forest into the default overworld generation and to replace some Taiga areas").define("addVampireForestToOverworld", true);
            vampireForestWeight_terrablender = builder.comment("Only considered if terrablender installed. Heigher values increase Vampirism region weight (likelyhood to appear)").defineInRange("vampireForestWeight_terrablender", 2, 1, 1000);
            enableHunterTentGeneration = builder.comment("Control hunter camp generation. If disabled you should set hunterSpawnChance to 75.").define("enableHunterTentGeneration", true);
            useVanillaCampfire = builder.comment("Use the vanilla campfire block instead of Vampirism's much cooler one").define("useVanillaCampfire", false);


            builder.push("village");
            villageTotemWeight = builder.comment("Weight of the Totem Building inside the Village").defineInRange("totemWeight", 20, 0, 140);
            villageTotemFactionChance = builder.comment("Chance for a totem to have a faction after generation").defineInRange("villageTotemFactionChance", 0.6, 0, 1);
            villageHunterTrainerWeight = builder.comment("Weight of the Hunter Trainer Building inside the Village").defineInRange("villageHunterTrainerWeight", 50, 0, 140);
            villageReplaceTemples = builder.comment("Whether village Temples should be replaced with versions that contain church altars.").define("villageReplaceTemples", true);
            builder.pop();

            builder.pop();
            builder.pop();
        }

    }
}
