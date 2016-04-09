package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.Logger;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.converted.BiteableRegistry;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.entity.player.actions.ActionRegistry;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.skills.SkillRegistry;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.network.ModGuiHandler;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.VampirismWorldGen;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.awt.*;
import java.io.File;

/**
 * Main class for Vampirism
 * TODO readd "required-after:teamlapen-lib;"
 */
@Mod(modid = REFERENCE.MODID, name = REFERENCE.NAME, version = REFERENCE.VERSION, acceptedMinecraftVersions = "[1.9]", dependencies = "required-after:Forge@[" + REFERENCE.FORGE_VERSION_MIN + ",);", guiFactory = "de.teamlapen.vampirism.client.core.ModGuiFactory", updateJSON = REFERENCE.VERSION_UPDATE_FILE)
public class VampirismMod {

    public final static Logger log = new Logger(REFERENCE.MODID, "de.teamlapen.vampirism");
    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;
    @SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.proxy.ServerProxy")
    public static IProxy proxy;
    public static boolean inDev = false;
    public static AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static CreativeTabs creativeTab = new CreativeTabs(REFERENCE.MODID) {
        @Override
        public Item getTabIconItem() {
            return ModItems.vampireFang;
        }
    };

    public static boolean isRealism() {
        return Configs.realism_mode;
    }

    private VersionChecker.VersionInfo versionInfo;

    public VersionChecker.VersionInfo getVersionInfo() {
        return versionInfo;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        versionInfo = VersionChecker.executeVersionCheck(REFERENCE.VERSION_UPDATE_FILE, REFERENCE.VERSION.equals("@VERSION@") ? "0.0.0-Test" : REFERENCE.VERSION);

        ModEventHandler eventHandler = new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        MinecraftForge.TERRAIN_GEN_BUS.register(eventHandler);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());

        GameRegistry.registerWorldGenerator(new VampirismWorldGen(), 1000);
        HelperRegistry.registerPlayerEventReceivingCapability(VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability(HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability(ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability(VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability(FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);
        Achievements.registerAchievement();
        proxy.onInitStep(IInitListener.Step.INIT, event);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new VampirismCommand());
        event.registerServerCommand(new TestCommand());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        finishAPI();
        proxy.onInitStep(IInitListener.Step.POST_INIT, event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        checkDevEnv();
        HunterPlayer.registerCapability();
        VampirePlayer.registerCapability();
        FactionPlayerHandler.registerCapability();
        ExtendedCreature.registerCapability();

        setupAPI1();
        Configs.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        Balance.init(new File(event.getModConfigurationDirectory(), REFERENCE.MODID), inDev);
        setupAPI2();


        dispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
        proxy.onInitStep(IInitListener.Step.PRE_INIT, event);
        VampireActions.registerDefaultActions();
        HunterActions.registerDefaultActions();
        VampireSkills.registerVampireSkills();


    }

    private void checkDevEnv() {
        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            inDev = true;
            log.inDev = true;
        }
    }

    private void finishAPI() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((BiteableRegistry) VampirismAPI.biteableRegistry()).finishRegistration(Balance.mobProps.CONVERTED_MOB_DEFAULT_DMG);
        ((ActionRegistry) VampirismAPI.actionRegistry()).finish();
        ((SkillRegistry) VampirismAPI.skillRegistry()).finish();
    }

    /**
     * Setup API during pre-init before configs are loaded
     */
    private void setupAPI1() {
        FactionRegistry factionRegistry = new FactionRegistry();
        SundamageRegistry sundamageRegistry = new SundamageRegistry();
        BiteableRegistry biteableRegistry = new BiteableRegistry();
        ActionRegistry actionRegistry = new ActionRegistry();
        SkillRegistry skillRegistry = new SkillRegistry();
        VampirismAPI.setUp(factionRegistry, sundamageRegistry, biteableRegistry, actionRegistry, skillRegistry);
        VReference.VAMPIRE_FACTION = factionRegistry.registerPlayableFaction("Vampire", IVampirePlayer.class, 0XFF780DA3, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.CAP, REFERENCE.HIGHEST_VAMPIRE_LEVEL);
        VReference.VAMPIRE_FACTION.setChatColor(TextFormatting.DARK_PURPLE).setUnlocalizedName("text.vampirism.vampire", "text.vampirism.vampires");
        VReference.HUNTER_FACTION = factionRegistry.registerPlayableFaction("Hunter", IHunterPlayer.class, Color.BLUE.getRGB(), REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.CAP, REFERENCE.HIGHEST_HUNTER_LEVEL);
        VReference.HUNTER_FACTION.setChatColor(TextFormatting.DARK_BLUE).setUnlocalizedName("text.vampirism.hunter", "text.vampirism.hunters");
        biteableRegistry.setDefaultConvertingHandlerCreator(new BiteableRegistry.ICreateDefaultConvertingHandler() {
            @Override
            public IConvertingHandler create(IConvertingHandler.IDefaultHelper helper) {
                return new DefaultConvertingHandler(helper);
            }
        });//DefaultConvertingHandler::new

        VReference.hunterCreatureType = EnumHelper.addCreatureType("vampirism:hunter", IHunter.class, 30, Material.air, false, false);
        VReference.vampireCreatureType = EnumHelper.addCreatureType("vampirism:vampire", IVampire.class, 30, Material.air, false, false);
        VReference.vampirePlantType = EnumPlantType.getPlantType("vampirism_vampire");
    }

    /**
     * Setup API during pre-init after configs are loaded
     */
    private void setupAPI2() {

    }

}
