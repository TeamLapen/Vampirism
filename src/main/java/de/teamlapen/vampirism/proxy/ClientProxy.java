package de.teamlapen.vampirism.proxy;

import com.mojang.authlib.GameProfile;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.client.VIngameOverlays;
import de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.LogBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.client.core.*;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.vampirism.client.gui.overlay.*;
import de.teamlapen.vampirism.client.gui.screens.*;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.client.renderer.blockentity.ModBlockEntityItemRenderer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.network.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.vampirism.util.PlayerModelType;
import de.teamlapen.vampirism.util.PlayerSkinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.event.lifecycle.ParallelDispatchEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Clientside Proxy
 */
public class ClientProxy extends CommonProxy {
    private final static Logger LOGGER = LogManager.getLogger(ClientProxy.class);
    private VampirismHUDOverlay overlay;
    private CustomBossEventOverlay bossInfoOverlay;
    private RenderHandler renderHandler;
    private Map<UUID, ResourceKey<SoundEvent>> bossEventSounds = new HashMap<>();
    private ModBlockEntityItemRenderer blockEntityItemRenderer;

    public ClientProxy() {
        //Minecraft.instance is null during runData.
        //noinspection ConstantConditions
        Minecraft instance = Minecraft.getInstance();
        if (instance != null) {
            this.renderHandler = new RenderHandler(instance);
            NeoForge.EVENT_BUS.register(this.renderHandler);
            ((ReloadableResourceManager) instance.getResourceManager()).registerReloadListener(this.renderHandler); // Must be added before initial resource manager load
        }
    }

    public void clearBossBarOverlay() {
        this.bossInfoOverlay.clear();
    }

    @Override
    public void displayNameSwordScreen(ItemStack stack) {
        openScreen(new NameSwordScreen(stack));
    }

    @Override
    public void displayRevertBackScreen() {
        openScreen(new RevertBackScreen());
    }

    @Override
    public void displayVampireMinionAppearanceScreen(VampireMinionEntity entity) {
        openScreen(new VampireMinionAppearanceScreen(entity, Minecraft.getInstance().screen));
    }

    @Override
    public void displayVampireMinionStatsaScreen(VampireMinionEntity entity) {
        openScreen(new VampireMinionStatsScreen(entity, Minecraft.getInstance().screen));
    }

    @Override
    public void displayHunterMinionAppearanceScreen(HunterMinionEntity entity) {
        openScreen(new HunterMinionAppearanceScreen(entity, Minecraft.getInstance().screen));
    }

    @Override
    public void displayHunterMinionStatsScreen(HunterMinionEntity entity) {
        openScreen(new HunterMinionStatsScreen(entity, Minecraft.getInstance().screen));
    }

    @Override
    public void obtainPlayerSkins(GameProfile profile, @NotNull Consumer<Pair<ResourceLocation, PlayerModelType>> callback) {
        PlayerSkinHelper.obtainPlayerSkinPropertiesAsync(profile, callback);
    }

    @Nullable
    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        HitResult r = Minecraft.getInstance().hitResult;
        if (r instanceof EntityHitResult) return ((EntityHitResult) r).getEntity();
        return null;
    }

    @Override
    public void handleSleepClient(@NotNull Player player) {
        if (player.isSleeping()) {
            player.getSleepingPos().ifPresent(pos -> {
                if (player.level().getBlockState(pos).getBlock() instanceof TentBlock) {
                    TentBlock.setTentSleepPosition(player, pos, player.level().getBlockState(pos).getValue(POSITION), player.level().getBlockState(pos).getValue(FACING));
                } else if (player.level().getBlockState(pos).getBlock() instanceof CoffinBlock) {
                    CoffinBlock.setCoffinSleepPosition(player, pos, player.level().getBlockState(pos));
                }
            });
        }
    }

    @Override
    public void handleUpdateMultiBossInfoPacket(@NotNull ClientboundUpdateMultiBossEventPacket msg) {
        this.bossInfoOverlay.read(msg);
    }

    @Override
    public void onInitStep(@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        super.onInitStep(step, event);
        switch (step) {
            case CLIENT_SETUP -> {
                Minecraft instance = Minecraft.getInstance();
                this.overlay = new VampirismHUDOverlay(instance);
                registerSubscriptions();
                ModBlocksRender.register();
                event.enqueueWork(() -> {
                    Sheets.addWoodType(LogBlock.DARK_SPRUCE);
                    Sheets.addWoodType(LogBlock.CURSED_SPRUCE);
                });
            }
            case LOAD_COMPLETE -> {
                event.enqueueWork(ModItemsRender::registerItemModelPropertyUnsafe);
            }
            default -> {
            }
        }
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {
        if (overlay != null) overlay.makeRenderFullColor(ticksOn, ticksOff, color);
    }

    @Override
    public void showDBNOScreen(@NotNull Player playerEntity, @Nullable Component deathMessage) {
        if (playerEntity == Minecraft.getInstance().player && !playerEntity.isDeadOrDying()) {
            openScreen(new DBNOScreen(deathMessage));
        }
    }

    @Override
    public void sendToServer(CustomPacketPayload packetPayload) {
        Minecraft.getInstance().getConnection().send(packetPayload);
    }

    private void registerSubscriptions() {
        NeoForge.EVENT_BUS.register(this.overlay);
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new ScreenEventHandler());
        NeoForge.EVENT_BUS.register(new ModKeys());
    }

    @Override
    public void setupAPIClient() {
        VIngameOverlays.FACTION_RAID_BAR_ELEMENT = this.bossInfoOverlay = new CustomBossEventOverlay();
        VIngameOverlays.BLOOD_BAR_ELEMENT = new BloodBarOverlay();
        VIngameOverlays.FACTION_LEVEL_ELEMENT = new FactionLevelOverlay();
        VIngameOverlays.ACTION_COOLDOWN_ELEMENT = new ActionCooldownOverlay();
        VIngameOverlays.ACTION_DURATION_ELEMENT = new ActionDurationOverlay();
    }

    /**
     * copied but which much lesser particles
     */
    @Override
    public void spawnParticles(Level world, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (!(world instanceof ClientLevel)) return;
        VoxelShape voxelshape = state.getShape(world, pos);
        voxelshape.forAllBoxes((p_199284_3_, p_199284_5_, p_199284_7_, p_199284_9_, p_199284_11_, p_199284_13_) -> {
            double d1 = Math.min(1.0D, p_199284_9_ - p_199284_3_);
            double d2 = Math.min(1.0D, p_199284_11_ - p_199284_5_);
            double d3 = Math.min(1.0D, p_199284_13_ - p_199284_7_);
            int i = Math.max(2, Mth.ceil(d1 / 0.25D));
            int j = Math.max(2, Mth.ceil(d2 / 0.25D));
            int k = Math.max(2, Mth.ceil(d3 / 0.25D));

            for (int l = 0; l < i / 2; ++l) {
                for (int i1 = 0; i1 < j / 2; ++i1) {
                    for (int j1 = 0; j1 < k / 2; ++j1) {
                        double d4 = ((double) l + 0.5D) / (double) i;
                        double d5 = ((double) i1 + 0.5D) / (double) j;
                        double d6 = ((double) j1 + 0.5D) / (double) k;
                        double d7 = d4 * d1 + p_199284_3_;
                        double d8 = d5 * d2 + p_199284_5_;
                        double d9 = d6 * d3 + p_199284_7_;
                        Minecraft.getInstance().particleEngine.add((new TerrainParticle((ClientLevel) world, (double) pos.getX() + d7, (double) pos.getY() + d8, (double) pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state)));
                    }
                }
            }

        });
    }

    @Override
    public void endBloodVisionBatch() {
        this.renderHandler.endBloodVisionBatch();
    }

    public static void runOnRenderThread(Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }

    public static void openScreen(Screen screen) {
        runOnRenderThread(() -> Minecraft.getInstance().setScreen(screen));
    }

    public static ClientProxy get() {
        return (ClientProxy) VampirismMod.proxy;
    }

    public ModBlockEntityItemRenderer getBlockEntityItemRenderer() {
        return blockEntityItemRenderer;
    }

    @Override
    public void addBossEventSound(UUID bossEventUuid, ResourceKey<SoundEvent> sound) {
        this.bossEventSounds.put(bossEventUuid, sound);
    }

    public ResourceKey<SoundEvent> getBossEventSound(UUID bossEventUuid) {
        return this.bossEventSounds.get(bossEventUuid);
    }

    public void registerBlockEntityItemRenderer() {
        this.blockEntityItemRenderer = new ModBlockEntityItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
}
