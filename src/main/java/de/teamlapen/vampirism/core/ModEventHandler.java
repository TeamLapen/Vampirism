package de.teamlapen.vampirism.core;

import com.google.common.base.Predicate;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.util.DaySleepHelper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.ModWorldEventListener;
import de.teamlapen.vampirism.world.villages.VampirismVillageCollection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.EntitySelectorEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {
    private final static String TAG = "EventHandler";

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equalsIgnoreCase(REFERENCE.MODID)) {
            VampirismMod.log.i(TAG, "Configuration (%s) changed", e.getConfigID());
            Configs.onConfigurationChanged();
            Balance.onConfigurationChanged();
        }
    }

    @SubscribeEvent
    public void onEntitySelector(EntitySelectorEvent event) {
        Map<String, String> arguments = event.getMap();
        String faction = arguments.get("vampirism:faction");

        if (faction != null) {
            final boolean invert = faction.startsWith("!");

            if (invert) {
                faction = faction.substring(1);
            }

            IFaction[] factions = VampirismAPI.factionRegistry().getFactions();
            boolean found = false;
            for (final IFaction f : factions) {
                if (f.name().equalsIgnoreCase(faction)) {
                    event.addPredicate(new Predicate<Entity>() {
                        @Override
                        public boolean apply(@Nullable Entity input) {
                            if (input instanceof IFactionEntity) {
                                boolean flag1 = f.equals(((IFactionEntity) input).getFaction());
                                return invert != flag1;
                            } else if (f instanceof IPlayableFaction && input instanceof EntityPlayer) {
                                boolean flag1 = FactionPlayerHandler.get((EntityPlayer) input).isInFaction((IPlayableFaction) f);
                                return invert != flag1;
                            }
                            return invert;
                        }
                    });
                    found = true;
                    break;
                }
            }
            if (!found) {
                //Prevents selection of all entities if mistyped
                event.addPredicate(new Predicate<Entity>() {
                    @Override
                    public boolean apply(@Nullable Entity input) {
                        return false;
                    }
                });
                event.getSender().addChatMessage(new TextComponentString("Unknown faction: " + faction));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInitMapGen(InitMapGenEvent event) {
        if (event.getType().equals(InitMapGenEvent.EventType.VILLAGE) && Configs.village_modify) {
            if (event.getNewGen() != event.getOriginalGen()) {
                VampirismMod.log.w("VillageGen", "The village map generator was overwritten by another mod. There might be a problem! \n The new generator class is " + event.getNewGen().getClass().getCanonicalName());
            }
            ModVillages.modifyVillageSize(event.getNewGen());
        }
    }

    @SubscribeEvent
    public void onPlayerJoinedWorld(EntityJoinWorldEvent entityJoinWorldEvent) {
        if ((entityJoinWorldEvent.getEntity() instanceof EntityPlayer)) {
            if (VampirismMod.proxy.isClientPlayerNull() || VampirismMod.proxy.isPlayerThePlayer((EntityPlayer) entityJoinWorldEvent.getEntity())) {
                //Did not find a better position to place this, since onPostInit is to early
                FakeNightVisionPotion.replaceNightVision();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) VampirismMod.log.w(TAG, "Version check is not finished yet");
        if (!Configs.disable_versionCheck && versionInfo.isNewVersionAvailable()) {
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || UtilLib.isPlayerOp(event.player) || event.player.getRNG().nextInt(5) == 0) {
                if (event.player.getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    //Inspired by @Vazikii's useful message
                    event.player.addChatComponentMessage(new TextComponentTranslation("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name));
                    String template = I18n.translateToLocal("text.vampirism.update_message");
                    template = template.replaceAll("@download@", newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl()).replaceAll("@forum@", versionInfo.getHomePage());
                    ITextComponent component = ITextComponent.Serializer.jsonToComponent(template);
                    event.player.addChatComponentMessage(component);
                }
            }

        }
        if (Configs.updated_vampirism) {
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || UtilLib.isPlayerOp(event.player)) {


                event.player.addChatComponentMessage(new TextComponentString("It looks like you have updated Vampirism"));
                event.player.addChatComponentMessage(new TextComponentString("Please consider resetting the balance values to the updated ones, using " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance all'" + TextFormatting.RESET));
                event.player.addChatComponentMessage(new TextComponentString("For more information use " + TextFormatting.DARK_GREEN + "'/vampirism resetBalance help'" + TextFormatting.RESET));
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new ModWorldEventListener(event.getWorld().provider.getDimension()));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) {
            DaySleepHelper.checkSleepWorld(event.world);
            VampirismVillageCollection.get(event.world).tick();
        }
    }
}
