package de.teamlapen.vampirism;

import de.teamlapen.sync.SyncRegistry;
import de.teamlapen.sync.common.entities.IPlayerEventListener;
import de.teamlapen.sync.common.storage.IAttachedSyncable;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.CommonServices;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.common.proxy.IProxy;
import de.teamlapen.vampirism.server.proxy.ServerProxy;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForgeMod;

/**
 * Main class for Vampirism
 */
@Mod(REFERENCE.MODID)
public class VampirismMod {

    public static final IProxy proxy = FMLEnvironment.getDist() == Dist.CLIENT ? VampirismModClient.getProxy() : new ServerProxy();
    public static boolean inDev = false;
    public static boolean inDataGen = false;
    private static CommonServices SERVICES;


    public VampirismMod(IEventBus modEventBus, ModContainer modContainer) {
        checkEnv();

        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);

        ShapedRecipePattern.setCraftingSize(4, 4);
        NeoForgeMod.enableMergedAttributeTooltips();

        SERVICES = new CommonServices(modContainer);
        SERVICES.register(modEventBus);
    }

    public static CommonServices services() {
        return SERVICES;
    }

    private void checkEnv() {
        String launchTarget = System.getProperty("vampirism_target");
        inDev = !FMLEnvironment.isProduction();
        if (launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    @SuppressWarnings("unchecked")
    private void enqueueIMC(final InterModEnqueueEvent event) {
        SyncRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        SyncRegistry.registerPlayerEventReceivingCapability((AttachmentType<IPlayerEventListener>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        SyncRegistry.registerSyncableEntityCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.EXTENDED_CREATURE.get(), ExtendedCreature.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.VAMPIRE_PLAYER.get(), VampirePlayer.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.HUNTER_PLAYER.get(), HunterPlayer.class);
        SyncRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.FACTION_PLAYER_HANDLER.get(), FactionPlayerHandler.class);
    }

    private void processIMC(final InterModProcessEvent event) {
        CrossbowArrowHandler.collectCrossbowArrows();
    }

}
