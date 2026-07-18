package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.core.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Renders the environmental breakdown of the Velmorra dimension after Dracula's death: closing blood-red fog,
 * ambient rumbling and drifting ash. Driven by the collapse progress synced via
 * {@link de.teamlapen.vampirism.common.network.packets.client.ClientboundVelmorraCollapsePacket}.
 */
public class VelmorraCollapseHandler {

    private float targetProgress = -1;
    private float smoothedProgress = -1;
    private int rumbleCooldown = 0;

    public void setProgress(float progress) {
        this.targetProgress = progress;
        if (this.smoothedProgress < 0) {
            this.smoothedProgress = progress;
        }
    }

    private boolean isCollapsing() {
        ClientLevel level = Minecraft.getInstance().level;
        return this.smoothedProgress >= 0 && level != null && level.dimension().equals(ModDimensions.VELMORRA_LEVEL);
    }

    /** Ease-in so the last two minutes get dramatic */
    private float easedProgress() {
        float p = Mth.clamp(this.smoothedProgress, 0, 1);
        return p * p;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.@NotNull Pre event) {
        if (!isCollapsing()) return;
        this.smoothedProgress += (this.targetProgress - this.smoothedProgress) * 0.05f;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null || mc.isPaused()) return;

        float p = easedProgress();
        RandomSource random = player.getRandom();

        if (--this.rumbleCooldown <= 0) {
            this.rumbleCooldown = 300 - (int) (200 * p) + random.nextInt(100);
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), ModSounds.VELMORRA_RUMBLE.get(), SoundSource.AMBIENT, 0.5f + 1.5f * p, 0.5f + random.nextFloat() * 0.3f, false);
        }

        int particles = (int) (p * 5);
        for (int i = 0; i < particles; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 32;
            double y = player.getY() + (random.nextDouble() - 0.3) * 16;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 32;
            level.addParticle(random.nextInt(4) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.ASH, x, y, z, 0, -0.05, 0);
        }
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.@NotNull RenderFog event) {
        if (!isCollapsing()) return;
        float p = easedProgress();
        switch (event.getType()) {
            case ATMOSPHERIC -> {
                event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), Mth.lerp(p, 96f, 12f)));
                event.setNearPlaneDistance(Math.min(event.getNearPlaneDistance(), Mth.lerp(p, 10f, 2f)));
            }
        }
    }

    @SubscribeEvent
    public void onComputeFogColor(ViewportEvent.@NotNull ComputeFogColor event) {
        if (!isCollapsing()) return;
        float p = easedProgress();
        event.setRed(Mth.lerp(p, event.getRed(), 0.16f));
        event.setGreen(Mth.lerp(p, event.getGreen(), 0.01f));
        event.setBlue(Mth.lerp(p, event.getBlue(), 0.03f));
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.@NotNull Load event) {
        this.targetProgress = -1;
        this.smoothedProgress = -1;
        this.rumbleCooldown = 0;
    }
}
