package de.teamlapen.vampirism.client.renderer.bloodvision;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.faction.client.IMinecraftAccessor;
import de.teamlapen.vampirism.client.OptifineHandler;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.client.renderer.bloodvision.entries.BloodEntityEntry;
import de.teamlapen.vampirism.client.renderer.bloodvision.entries.IEntityEntry;
import de.teamlapen.vampirism.client.renderer.bloodvision.entries.PoisonBloodEntry;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.MixinHooks;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.misc.extension.client.ILevelRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.SequencedMap;
import java.util.function.Consumer;

public class BloodVisionRenderer implements IMinecraftAccessor {
    private static final int ENTITY_NEAR_SQ_DISTANCE = 900;
    private static final int BLOOD_VISION_FADE_TICKS = 80;
    private boolean reducedBloodVision = false;
    private int bloodVisionTicks = 0;
    private int lastBloodVisionTicks = 0;
    private boolean hasGarlicVision;

    private final SubmitNodeStorage nodeCollector = new SubmitNodeStorage();
    private final OutlineBufferSource noOp = new NoOpOutlineBufferSource();
    @UnknownNullability
    private ColoredBufferSource bloodVisionBuffer;
    private List<IEntityEntry> cachedEntityRenderStates = List.of();


    public void create(RegisterRenderBuffersEvent event) {
        SequencedMap<RenderType, ByteBufferBuilder> sequencedmap = Util.make(new Object2ObjectLinkedOpenHashMap<>(), p_465615_ -> {
            p_465615_.put(ModRenderPipelines.solidTransparencyEntity(), new ByteBufferBuilder(786432));
        });
        this.bloodVisionBuffer = new ColoredBufferSource(sequencedmap, new ByteBufferBuilder(786432));
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (shouldRenderBloodVision()) {
            this.reducedBloodVision = OptifineHandler.isShaders();
            if (this.reducedBloodVision) {
                MixinHooks.enforcingGlowing_bloodVision = true;
            }
        }
    }


    @SubscribeEvent
    public void onExtract(ExtractLevelRenderStateEvent event) {
        this.hasGarlicVision = VampirePlayer.get(player()).getSkillProperties().blood_vision_garlic;
        this.cachedEntityRenderStates = event.getRenderState().entityRenderStates.stream().filter(x -> x.distanceToCameraSq < ENTITY_NEAR_SQ_DISTANCE * 2).filter(x -> x instanceof LivingEntityRenderState).mapMulti(this::createEntry).toList();
    }

    private void createEntry(EntityRenderState renderState, Consumer<IEntityEntry> consumer) {
        Integer blood = renderState.getRenderDataOrDefault(ModEntityRenderStates.BLOOD, 0);
        Boolean poisonBlood = renderState.getRenderDataOrDefault(ModEntityRenderStates.POISON_BLOOD, false);
        if (blood > 0 && !poisonBlood) {
            consumer.accept(new BloodEntityEntry(renderState, (float) blood / renderState.getRenderDataOrDefault(ModEntityRenderStates.MAX_BLOOD, 1)));
        } else if (hasGarlicVision && (poisonBlood || renderState.getRenderDataOrDefault(ModEntityRenderStates.HUNTER, false))) {
            consumer.accept(new PoisonBloodEntry(renderState));
        } else {
//            consumer.accept(new OtherEntityEntry(renderState));
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderLevelStageEvent.AfterWeather event) {
        if (shouldRenderBloodVision() && !reducedBloodVision) {
            PostChain blur = this.mc().getShaderManager().getPostChain(Identifier.withDefaultNamespace("blur"), LevelTargetBundle.MAIN_TARGETS);
//                blur.setUniform("Radius", 12 * getBloodVisionProgress(partalTicks));
            blur.process(Minecraft.getInstance().getMainRenderTarget(), Minecraft.getInstance().gameRenderer.resourcePool);

            var poseStack = new PoseStack();
            ILevelRenderer levelRenderer = event.getLevelRenderer();

            var parts = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher().vampirism$modelPartFeatureRenderer();
            var models = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher().vampirism$modelFeatureFeatureRenderer();

            for (IEntityEntry entry : this.cachedEntityRenderStates) {
                double distanceToCameraSq = entry.renderState().distanceToCameraSq;

                float perc = getBloodVisionProgress();

                if (distanceToCameraSq > ENTITY_NEAR_SQ_DISTANCE) {
                    perc *= (float) (1- (distanceToCameraSq - ENTITY_NEAR_SQ_DISTANCE) / ENTITY_NEAR_SQ_DISTANCE);
                }

                this.bloodVisionBuffer.setColor(ARGB.color(perc, entry.color()));

                submitEntities(poseStack, event.getLevelRenderState(), List.of(entry.renderState()), this.nodeCollector, levelRenderer.vampirism$entityRenderDispatcher());

                for (SubmitNodeCollection value : this.nodeCollector.getSubmitsPerOrder().values()) {
                    parts.render(value, this.bloodVisionBuffer, this.noOp, this.bloodVisionBuffer);
                    models.render(value, this.bloodVisionBuffer, this.noOp, this.bloodVisionBuffer);
                }

                this.nodeCollector.clear();
                this.bloodVisionBuffer.endBatch();
            }

            if (!poseStack.isEmpty()) {
                throw new IllegalStateException("Pose stack not empty");
            }

            this.cachedEntityRenderStates = List.of();
        }
    }


    private void submitEntities(PoseStack poseStack, LevelRenderState renderState, List<EntityRenderState> states, SubmitNodeCollector nodeCollector, EntityRenderDispatcher dispatcher) {
        Vec3 vec3 = renderState.cameraRenderState.pos;
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();

        for (EntityRenderState entityrenderstate : states) {
            dispatcher
                    .submit(
                            entityrenderstate,
                            renderState.cameraRenderState,
                            entityrenderstate.x - d0,
                            entityrenderstate.y - d1,
                            entityrenderstate.z - d2,
                            poseStack,
                            nodeCollector
                    );
        }
    }


    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        if (this.level() == null || this.player() == null || !player().isAlive()) return;
        this.lastBloodVisionTicks = this.bloodVisionTicks;
        VampirePlayer vampire = VampirePlayer.get(player());
        if (vampire.getSkillProperties().blood_vision && !ModConfig.client().disableBloodVisionRendering.get() && !vampire.isGettingSundamage(level())) {
            if (this.bloodVisionTicks < BLOOD_VISION_FADE_TICKS) {
                this.bloodVisionTicks++;

            }
        } else {
            if (this.bloodVisionTicks > 0) {
                this.bloodVisionTicks -= 2;
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.bloodVisionTicks = 0;
        this.lastBloodVisionTicks = 0;
    }

    public boolean shouldRenderBloodVision() {
        return this.bloodVisionTicks > 0 && this.player() != null;
    }

    private float getBloodVisionProgress() {
        return (bloodVisionTicks + (bloodVisionTicks - lastBloodVisionTicks)) / (float) BLOOD_VISION_FADE_TICKS;
    }
}
