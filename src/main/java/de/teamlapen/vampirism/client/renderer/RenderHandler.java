package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.renderer.entity.state.IVampirismRenderState;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.items.CrucifixItem;
import de.teamlapen.vampirism.mixin.client.accessor.CameraAccessor;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Handle most general rendering related stuff
 */
@SuppressWarnings("unused")
public class RenderHandler {
    @NotNull
    private final Minecraft mc;


    private final int VAMPIRE_BIOME_FADE_TICKS = 60;
    private final Logger LOGGER = LogManager.getLogger();

    private int vampireBiomeTicks = 0;
    /**
     * If inside a foggy area.
     * Only updated every n ticks
     */
    private boolean insideFog = false;

    private float vampireBiomeFogDistanceMultiplier = 1;

    public RenderHandler(@NotNull Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.@NotNull ComputeCameraAngles event) {
        if (VampirismConfig.SERVER.preventRenderingDebugBoundingBoxes.get()) {
            Minecraft.getInstance().getEntityRenderDispatcher().setRenderHitBoxes(false);
        }
        if (event.getCamera().getEntity() instanceof LivingEntity && ((LivingEntity) event.getCamera().getEntity()).isSleeping()) {
            ((LivingEntity) event.getCamera().getEntity()).getSleepingPos().map(pos -> event.getCamera().getEntity().level().getBlockState(pos)).filter(blockState -> blockState.getBlock() instanceof CoffinBlock).ifPresent(blockState -> {
                if (blockState.getValue(CoffinBlock.VERTICAL)) {
                    ((CameraAccessor) event.getCamera()).invoke_move(0.2f, -0.2f, 0);
                } else {
                    ((CameraAccessor) event.getCamera()).invoke_move(0, -0.2f, 0);
                }
            });
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        if (mc.level == null || mc.player == null || !mc.player.isAlive()) return;
        VampirePlayer vampire = VampirePlayer.get(mc.player);

        //Vampire biome/village fog
        if (mc.player.tickCount % 10 == 0) {
            if ((VampirismConfig.CLIENT.renderVampireForestFog.get() || VampirismConfig.SERVER.enforceRenderForestFog.get()) && (Helper.isEntityInArtificalVampireFogArea(mc.player) || Helper.isEntityInVampireBiome(mc.player))) {
                insideFog = true;
                vampireBiomeFogDistanceMultiplier = vampire.getLevel() > 0 ? 2 : 1;
                vampireBiomeFogDistanceMultiplier += vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.VISTA) ? VampirismConfig.BALANCE.vrVistaMod.get().floatValue() : 0;

                vampireBiomeFogDistanceMultiplier = VampirismEventFactory.fireVampireFogEvent(vampireBiomeFogDistanceMultiplier);

            } else {
                insideFog = false;
            }
        }
        if (insideFog) {
            if (vampireBiomeTicks < VAMPIRE_BIOME_FADE_TICKS) {
                vampireBiomeTicks++;
            }
        } else {
            if (vampireBiomeTicks > 0) {
                vampireBiomeTicks--;
            }
        }
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.@NotNull RenderFog event) {
        if (vampireBiomeTicks == 0) return;
        float f = ((float) VAMPIRE_BIOME_FADE_TICKS) / (float) vampireBiomeTicks / 1.5f;
        f *= vampireBiomeFogDistanceMultiplier;
        event.setNearPlaneDistance(switch (event.getMode()) {
            case FOG_TERRAIN -> Math.min(event.getFarPlaneDistance() * 0.75f, 6 * f);
            case FOG_SKY -> 0;
        });
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), 50 * f));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderHand(@NotNull RenderHandEvent event) {
        if (mc.player != null && mc.player.isAlive() && VampirismPlayerAttributes.get(mc.player).getVampSpecial().bat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.@NotNull Pre<Player, PlayerRenderState, PlayerModel> event) {
        var vampirism = ((IVampirismRenderState) event.getRenderState()).vampirism$attributes();
        if (vampirism != null && vampirism.getHuntSpecial().isDisguised()) {
            double dist = this.mc.player == null ? 0 : event.getRenderState().distanceToCameraSq;
            if (dist > 64) {
                event.setCanceled(true);
            } else if (dist > 16) {
                IItemWithTier.TIER hunterCoatTier = vampirism.getHuntSpecial().fullHunterCoat;
                if (hunterCoatTier == IItemWithTier.TIER.ENHANCED || hunterCoatTier == IItemWithTier.TIER.ULTIMATE) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderFirstPersonHand(@NotNull RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && event.getHand() == player.getUsedItemHand()) {
            if (event.getItemStack().getItem() instanceof CrucifixItem) {
                HumanoidArm humanoidarm = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
                int i = humanoidarm == HumanoidArm.RIGHT ? 1 : -1;
                event.getPoseStack().translate(((float) -i * 0.56F), -0.0, -0.2F);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.@NotNull Pre event) {
        IVampirismRenderState vampState = (IVampirismRenderState) event.getRenderState();
        VampirePlayerSpecialAttributes vAtt = vampState.vampirism$attributes().getVampSpecial();
        if (vAtt.isDBNO) {
            event.getPoseStack().translate(1.2, 0, 0);
            PlayerModel m = event.getRenderer().getModel();
            m.rightArm.visible = false;
            m.rightSleeve.visible = false;
            m.leftArm.visible = false;
            m.leftSleeve.visible = false;
            m.rightLeg.visible = false;
            m.leftLeg.visible = false;
            m.rightPants.visible = false;
            m.leftPants.visible = false;
        } else if (vampState.sleeping$inCoffin()) {
            //Shrink player, so they fit into the coffin model
            event.getPoseStack().scale(0.8f, 0.95f, 0.8f);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.vampireBiomeTicks = 0;
        this.insideFog = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.@NotNull Pre event) {
        IVampirismRenderState vampState = (IVampirismRenderState) event.getRenderState();
        VampirePlayerSpecialAttributes vAtt = vampState.vampirism$attributes().getVampSpecial();
        if (vAtt.invisible) {
            event.setCanceled(true);
        } else if (vAtt.bat) {
            event.setCanceled(true);
            var bat = vampState.vampirism$bat();

            float partialTicks = event.getPartialTick();

            bat.tickCount = (int) event.getRenderState().ageInTicks;
            bat.setInvisible(event.getRenderState().isInvisible);
            mc.getEntityRenderDispatcher().render(bat, 0, 0, 0, partialTicks, event.getPoseStack(), mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getPackedLightCoords(bat, partialTicks));
        }
    }

}
