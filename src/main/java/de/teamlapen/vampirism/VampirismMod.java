package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.Logger;
import de.teamlapen.lib.lib.util.ModCompatLoader;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.BloodGrinderValueLoader;
import de.teamlapen.vampirism.config.BloodValueLoader;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.VampirismEntitySelectors;
import de.teamlapen.vampirism.entity.action.EntityActionManager;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.inventory.HunterWeaponCraftingManager;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.modcompat.SpongeModCompat;
import de.teamlapen.vampirism.modcompat.guide.GuideAPICompat;
import de.teamlapen.vampirism.modcompat.jei.JEIModCompat;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.player.actions.ActionManager;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.skills.SkillManager;
import de.teamlapen.vampirism.player.vampire.BloodVision;
import de.teamlapen.vampirism.player.vampire.NightVision;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.potion.blood.BloodPotionRegistry;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.tileentity.TileTent;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.GarlicChunkHandler;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import de.teamlapen.vampirism.world.gen.structure.StructureManager;
import de.teamlapen.vampirism.world.loot.LootHandler;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.awt.*;
import java.io.File;

/**
 * Main class for Vampirism TODO readd "required-after:teamlapen-lib;"
 */
@Mod(modid = REFERENCE.MODID, name = REFERENCE.NAME, version = REFERENCE.VERSION, acceptedMinecraftVersions = "[1.12,)", dependencies = "required-after:forge@[" + REFERENCE.FORGE_VERSION_MIN + ",);after:guideapi", guiFactory = "de.teamlapen.vampirism.client.core.ModGuiFactory", updateJSON = REFERENCE.VERSION_UPDATE_FILE_FORGE)
public class VampirismMod {

    public final static Logger log = new Logger(REFERENCE.MODID, "de.teamlapen.vampirism");
    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static final CreativeTabs creativeTab = new CreativeTabs(REFERENCE.MODID) {

        @Override
        public ItemStack createIcon() {

            return new ItemStack(ModItems.vampire_fang);
        }
    };
    /**
     * Hunter creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private final static EnumCreatureType HUNTER_CREATURE_TYPE = EnumHelper.addCreatureType("VAMPIRISM_HUNTER", IHunterMob.class, 25, Material.AIR, false, false);
    /**
     * Vampire creatures are of this creature type. Use the instance in
     * {@link VReference} instead of this one. This is only here to init it as early
     * as possible
     */
    private static final EnumCreatureType VAMPIRE_CREATURE_TYPE = EnumHelper.addCreatureType("VAMPIRISM_VAMPIRE", IVampireMob.class, 30, Material.AIR, false, false);
    /**
     * Vampire creatures have this attribute Vampire creatures are of this creature
     * type. Use the instance in {@link VReference} instead of this one. This is
     * only here to init it as early as possible
     */
    private static final EnumCreatureAttribute VAMPIRE_CREATURE_ATTRIBUTE = EnumHelper.addCreatureAttribute("VAMPIRISM_VAMPIRE");
    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;
    @SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.proxy.ServerProxy")
    public static IProxy proxy;
    public static boolean inDev = false;

    public static boolean isRealism() {
        return Configs.realism_mode;
    }

    public final ModCompatLoader modCompatLoader = new ModCompatLoader(REFERENCE.MODID + "/vampirism_mod_compat.cfg");
    private final RegistryManager registryManager;
    private VersionChecker.VersionInfo versionInfo;

    public VampirismMod() {

        addModCompats();
        registryManager = new RegistryManager();
        MinecraftForge.EVENT_BUS.register(registryManager);
        setupAPI1();
    }

    public VersionChecker.VersionInfo getVersionInfo() {

        return versionInfo;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        finishAPI1();

        String currentVersion = "@VERSION@".equals(REFERENCE.VERSION) ? "0.0.0-test" : REFERENCE.VERSION;
        if (Configs.disable_versionCheck) {
            versionInfo = new VersionChecker.VersionInfo(currentVersion);
        } else {
            versionInfo = VersionChecker.executeVersionCheck(REFERENCE.VERSION_UPDATE_FILE, currentVersion, !inDev && !Configs.disable_collectVersionStat);
        }

        ModEventHandler eventHandler = new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        MinecraftForge.TERRAIN_GEN_BUS.register(eventHandler);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
        MinecraftForge.EVENT_BUS.register(LootHandler.getInstance());

        GameRegistry.registerWorldGenerator(VampirismWorldGen.getInstance(), 1000);
        HelperRegistry.registerPlayerEventReceivingCapability(VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability(HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability(ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability(VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);
        SupporterManager.getInstance().initAsync();
        VampireBookManager.getInstance().init();
        BloodPotions.register();
        StructureManager.init();
        VampirismEntitySelectors.registerSelectors();
        registryManager.onInitStep(IInitListener.Step.INIT, event);
        proxy.onInitStep(IInitListener.Step.INIT, event);
        modCompatLoader.onInitStep(IInitListener.Step.INIT, event);

        // Check for halloween special
        if (HalloweenSpecial.shouldEnable()) {
            HalloweenSpecial.enable();
            MinecraftForge.EVENT_BUS.register(new HalloweenSpecial());
        }
    }

    @Mod.EventHandler
    public void interModComm(FMLInterModComms.IMCEvent event) {
        IMCHandler.handleInterModMessage(event.getMessages());
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new VampirismCommand());
        event.registerServerCommand(new TestCommand());
        VampirismEntityRegistry.getBiteableEntryManager().initDynamic();
        BloodValueLoader.onServerStarting(event.getServer());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (!LootHandler.getInstance().checkAndResetInsertedAll()) {
            VampirismMod.log.w("LootTables", "-------------------------------");
            VampirismMod.log.w("LootTables", "Failed to inject all loottables");
            VampirismMod.log.w("LootTables", "-------------------------------");
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        BloodValueLoader.onServerStopping();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        finishAPI2();
        registryManager.onInitStep(IInitListener.Step.POST_INIT, event);
        proxy.onInitStep(IInitListener.Step.POST_INIT, event);
        modCompatLoader.onInitStep(IInitListener.Step.POST_INIT, event);

        if (inDev) {
            Tests.runBackgroundTests();
        }

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        checkDevEnv();
        HunterPlayer.registerCapability();
        VampirePlayer.registerCapability();
        FactionPlayerHandler.registerCapability();
        ExtendedCreature.registerCapability();
        VampirismVillage.registerCapability();

        setupAPI2();
        Configs.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        Balance.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        BloodValueLoader.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID));
        BloodGrinderValueLoader.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID));
        modCompatLoader.onInitStep(IInitListener.Step.PRE_INIT, event);
        setupAPI3();

        // Data Fixer
        ModFixs fixer = FMLCommonHandler.instance().getDataFixer().init(REFERENCE.MODID, 5);// Fixes that should have the id of the ModFix version when added.
        // If adding new fixes to this, bump ModFix version and use the same one for the fixer
        fixer.registerFix(FixTypes.ENTITY, ModEntities.getEntityIDFixer());
        fixer.registerFix(FixTypes.BLOCK_ENTITY, ModBlocks.getTileEntityIDFixer());
        fixer.registerFix(FixTypes.BLOCK_ENTITY, TileTent.getTentFixer());
        fixer.registerFix(FixTypes.ENTITY, ModEntities.getEntityCapabilityFixer());
        fixer.registerFix(FixTypes.PLAYER, ModEntities.getPlayerCapabilityFixer());

        dispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
        registryManager.onInitStep(IInitListener.Step.PRE_INIT, event);
        proxy.onInitStep(IInitListener.Step.PRE_INIT, event);
        ScoreboardUtil.init();

    }

    private void addModCompats() {
        modCompatLoader.addModCompat(new JEIModCompat());
        modCompatLoader.addModCompat(new SpongeModCompat());
        modCompatLoader.addModCompat(new GuideAPICompat());
    }

    private void checkDevEnv() {
        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            inDev = true;
            log.setDebug(true);
            if (FMLCommonHandler.instance().getSide().isClient()) {
                log.displayModID();
            }
        }
    }

    /**
     * Finish during init
     */
    private void finishAPI1() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((SkillManager) VampirismAPI.skillManager()).buildSkillTrees();
    }

    /**
     * Finish during post-init
     */
    private void finishAPI2() {
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).finishRegistration();
    }

    /**
     * Called during construction
     */
    private void setupAPI1() {
        FactionRegistry factionRegistry = new FactionRegistry();
        SundamageRegistry sundamageRegistry = new SundamageRegistry();
        VampirismEntityRegistry biteableRegistry = new VampirismEntityRegistry();
        ActionManager actionManager = new ActionManager();
        SkillManager skillManager = new SkillManager();
        GeneralRegistryImpl generalRegistry = new GeneralRegistryImpl();
        EntityActionManager entityActionManager = new EntityActionManager();

        biteableRegistry.setDefaultConvertingHandlerCreator(DefaultConvertingHandler::new);
        BloodPotionRegistry bloodPotionRegistry = new BloodPotionRegistry();
        VampirismAPI.setUpRegistries(factionRegistry, sundamageRegistry, biteableRegistry, actionManager, skillManager, generalRegistry, bloodPotionRegistry, entityActionManager);
        VampirismAPI.setUpAccessors(HunterWeaponCraftingManager.getInstance(), new GarlicChunkHandler.Provider(), AlchemicalCauldronCraftingManager.getInstance());
    }

    /**
     * Setup API during pre-init before configs are loaded
     */
    private void setupAPI2() {
        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction("Vampire", IVampirePlayer.class, 0XFF780DA3, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.CAP, REFERENCE.HIGHEST_VAMPIRE_LEVEL);
        VReference.VAMPIRE_FACTION.setChatColor(TextFormatting.DARK_PURPLE).setUnlocalizedName("text.vampirism.vampire", "text.vampirism.vampires");
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry().registerPlayableFaction("Hunter", IHunterPlayer.class, Color.BLUE.getRGB(), REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.CAP, REFERENCE.HIGHEST_HUNTER_LEVEL);
        VReference.HUNTER_FACTION.setChatColor(TextFormatting.DARK_BLUE).setUnlocalizedName("text.vampirism.hunter", "text.vampirism.hunters");
        VReference.HUNTER_CREATURE_TYPE = HUNTER_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_TYPE = VAMPIRE_CREATURE_TYPE;
        VReference.VAMPIRE_CREATURE_ATTRIBUTE = VAMPIRE_CREATURE_ATTRIBUTE;
    }

    /**
     * Setup API during pre-init after configs are loaded
     */
    private void setupAPI3() {
        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision("nightVision", new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision("bloodVision", new BloodVision());
    }

}
