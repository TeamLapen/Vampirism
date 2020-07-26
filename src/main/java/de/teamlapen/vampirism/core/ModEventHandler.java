package de.teamlapen.vampirism.core;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.modcompat.IntegrationsNotifier;
import de.teamlapen.vampirism.network.BloodValuePacket;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Permissions;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Handles all events used in central parts of the mod
 */
public class ModEventHandler {

    private final static Logger LOGGER = LogManager.getLogger(ModEventHandler.class);


    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.getState().getBlock().equals(Blocks.OAK_LEAVES)) {
            if (ModBiomes.vampire_forest.equals(event.getWorld().getBiome(event.getPos()))) {
                PlayerEntity p = event.getHarvester();
                if (p != null && p.getRNG().nextInt(VampirismConfig.BALANCE.dropOrchidFromLeavesChance.get()) == 0) {
                    event.getDrops().add(new ItemStack(ModBlocks.vampire_orchid, 1));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        VersionChecker.VersionInfo versionInfo = VampirismMod.instance.getVersionInfo();
        if (!versionInfo.isChecked()) LOGGER.warn("Version check is not finished yet");

        boolean isAdminLikePlayer = !ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || UtilLib.isPlayerOp(event.getPlayer());

        if (VampirismConfig.COMMON.versionCheck.get() && versionInfo.isNewVersionAvailable()) {
            if (isAdminLikePlayer || event.getPlayer().getRNG().nextInt(5) == 0) {
                if (event.getPlayer().getRNG().nextInt(4) == 0) {
                    VersionChecker.Version newVersion = versionInfo.getNewVersion();
                    event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.outdated", versionInfo.getCurrentVersion().name, newVersion.name), Util.field_240973_b_);
                    ITextComponent download = new TranslationTextComponent("text.vampirism.update_message.download").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, newVersion.getUrl() == null ? versionInfo.getHomePage() : newVersion.getUrl())).setUnderlined(true).func_240712_a_(TextFormatting.BLUE));
                    ITextComponent changelog = new TranslationTextComponent("text.vampirism.update_message.changelog").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vampirism changelog")).setUnderlined(true));
                    ITextComponent modpage = new TranslationTextComponent("text.vampirism.update_message.modpage").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, versionInfo.getHomePage())).setUnderlined(true).func_240712_a_(TextFormatting.BLUE));
                    event.getPlayer().sendMessage(new StringTextComponent("").func_230529_a_(download).func_230529_a_(new StringTextComponent(" ")).func_230529_a_(changelog).func_230529_a_(new StringTextComponent(" ")).func_230529_a_(modpage), Util.field_240973_b_);
                }
            }
        }
        if (isAdminLikePlayer) {
            List<String> mods = IntegrationsNotifier.shouldNotifyAboutIntegrations();
            if (!mods.isEmpty()) {
                event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.integrations_available.first"), Util.field_240973_b_);
                event.getPlayer().sendMessage(new StringTextComponent(TextFormatting.BLUE + TextFormatting.ITALIC.toString() + org.apache.commons.lang3.StringUtils.join(mods, ", ") + TextFormatting.RESET), Util.field_240973_b_);
                event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.integrations_available.download").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.INTEGRATIONS_LINK)).setUnderlined(true)), Util.field_240973_b_);
            }

            if (!ModList.get().isLoaded("guideapi-vp")) {
                if (VampirismConfig.SERVER.infoAboutGuideAPI.get()) {
                    event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.guideapi_available.first"), Util.field_240973_b_);
                    event.getPlayer().sendMessage(new TranslationTextComponent("text.vampirism.guideapi_available.download").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, REFERENCE.GUIDEAPI_LINK)).setUnderlined(true)), Util.field_240973_b_);

                    VampirismConfig.SERVER.infoAboutGuideAPI.set(false);
                }
            }
        }
        if (event.getPlayer().getRNG().nextInt(3) == 0) {
            event.getPlayer().sendStatusMessage(new StringTextComponent("You are playing an alpha version of Vampirism for 1.16, some things might not work yet. Please report any issues except for:").func_240699_a_(TextFormatting.RED), false);
            event.getPlayer().sendStatusMessage(new StringTextComponent("- Vampire forest is not generated in a normal overworld dimension"), false);
            event.getPlayer().sendStatusMessage(new StringTextComponent("- Sleeping in coffin might not work"), false);
        }

        VampirismMod.dispatcher.sendTo(new SkillTreePacket(VampirismMod.proxy.getSkillTree(false).getCopy()), (ServerPlayerEntity) event.getPlayer());

        @SuppressWarnings("unchecked")
        Pair<Map<ResourceLocation, Integer>, Integer>[] bloodValues = (Pair<Map<ResourceLocation, Integer>, Integer>[]) Array.newInstance(Pair.class, 3);
        bloodValues[0] = new Pair<>(((VampirismEntityRegistry) VampirismAPI.entityRegistry()).getBloodValues(), ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).getBloodMultiplier());
        bloodValues[1] = new Pair<>(BloodConversionRegistry.getItemValues(), BloodConversionRegistry.getItemMultiplier());
        bloodValues[2] = new Pair<>(BloodConversionRegistry.getFluidValues(), BloodConversionRegistry.getFluidDivider());

        VampirismMod.dispatcher.sendTo(new BloodValuePacket(bloodValues), (ServerPlayerEntity) event.getPlayer());


        if (!PermissionAPI.hasPermission(event.getPlayer(), Permissions.VAMPIRISM)) {
            event.getPlayer().sendMessage(new StringTextComponent("[" + TextFormatting.DARK_PURPLE + "Vampirism" + TextFormatting.RESET + "] It seems like the permission plugin used is not properly set up. Make sure all players have 'vampirism.*' for the mod to work (or at least '" + Permissions.VAMPIRISM + "' to suppress this warning)."), Util.field_240973_b_);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        VampirismAPI.getGarlicChunkHandler(event.getWorld()).clear();
        TotemTileEntity.clearCacheForDimension(event.getWorld().getWorld().func_234923_W_());
    }
}
