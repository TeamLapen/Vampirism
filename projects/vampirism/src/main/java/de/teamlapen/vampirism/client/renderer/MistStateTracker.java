package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-entity client state backing the mist effect: the fade envelope, a smoothed velocity, and the accumulated
 * flow offset that gives the volume its movement animation.
 * <p>
 * All three have to be tracked here rather than read off the entity, because none of them survive the trip to
 * other clients. Mist form is a lasting action but only the local player's action timers are synced, so the fade
 * has to be inferred from the mist flag itself - which every client does see for every player. And
 * {@code getDeltaMovement()} is only meaningful for entities this client simulates: remote players and
 * server-driven mobs are moved by position packets, so their delta movement reads as roughly zero, which is why
 * velocity here comes from actual position deltas instead.
 */
public class MistStateTracker {

    /**
     * Ticks the envelope takes to ramp fully in or out. Matches the feel of Dracula's own 300ms mist fade.
     */
    private static final float FADE_TICKS = 6.0f;
    /**
     * Per-tick weight of the newest velocity sample. Low enough that turning eases the wake around over a few
     * tenths of a second instead of snapping it to the new heading.
     */
    private static final float VELOCITY_SMOOTHING = 0.12f;
    /**
     * Fraction of the entity's travel that the noise field drifts backwards through the volume. Below 1 so the
     * mist trails behind without appearing pinned to the ground it is passing over.
     */
    private static final float FLOW_STRENGTH = 0.6f;

    private final Map<UUID, MistState> states = new HashMap<>();

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            this.states.clear();
            return;
        }
        if (minecraft.isPaused()) {
            return;
        }

        for (Entity entity : minecraft.level.entitiesForRendering()) {
            boolean misting = isInMistForm(entity);
            MistState state = this.states.get(entity.getUUID());
            if (state == null) {
                if (!misting) {
                    continue;
                }
                state = new MistState(entity.position());
                this.states.put(entity.getUUID(), state);
            }
            state.tick(entity.position(), misting);
        }

        // Drop entities that finished fading out. Anything that stopped rendering keeps its last state until it
        // decays on its own, which is what lets a mist cloud fade out rather than vanish when its entity unloads.
        this.states.values().removeIf(state -> state.fadeCurrent <= 0.0f);
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.states.clear();
    }

    private static boolean isInMistForm(Entity entity) {
        if (entity instanceof Player player) {
            return Helper.isVampire(player) && VampirePlayer.get(player).getSkillProperties().mist;
        }
        return entity instanceof Dracula dracula && dracula.getState() == DraculaState.MIST;
    }

    /**
     * @return 0 when no mist should be drawn for this entity, ramping to 1 while in mist form
     */
    public float getFade(Entity entity, float partialTick) {
        MistState state = this.states.get(entity.getUUID());
        return state == null ? 0.0f : Mth.lerp(partialTick, state.fadePrevious, state.fadeCurrent);
    }

    /**
     * @return smoothed movement in blocks per tick, derived from position deltas so it is correct for entities
     * this client does not simulate
     */
    public Vec3 getVelocity(Entity entity) {
        MistState state = this.states.get(entity.getUUID());
        return state == null ? Vec3.ZERO : state.velocity;
    }

    /**
     * @return how far the noise field has drifted through the volume since mist form began, in blocks
     */
    public Vec3 getFlow(Entity entity) {
        MistState state = this.states.get(entity.getUUID());
        return state == null ? Vec3.ZERO : state.flow;
    }

    private static class MistState {
        private float fadePrevious;
        private float fadeCurrent;
        private Vec3 lastPosition;
        private Vec3 velocity = Vec3.ZERO;
        private Vec3 flow = Vec3.ZERO;

        private MistState(Vec3 position) {
            this.lastPosition = position;
        }

        private void tick(Vec3 position, boolean misting) {
            this.fadePrevious = this.fadeCurrent;
            this.fadeCurrent = Mth.clamp(this.fadeCurrent + (misting ? 1.0f : -1.0f) / FADE_TICKS, 0.0f, 1.0f);

            Vec3 delta = position.subtract(this.lastPosition);
            this.lastPosition = position;
            // A teleport is not movement; letting it through would fire the wake off in a random direction.
            if (delta.lengthSqr() > 4.0) {
                delta = Vec3.ZERO;
            }
            this.velocity = this.velocity.scale(1.0 - VELOCITY_SMOOTHING).add(delta.scale(VELOCITY_SMOOTHING));
            // Integrated rather than derived per frame, so the drift is continuous even as the direction turns.
            this.flow = this.flow.add(this.velocity.scale(FLOW_STRENGTH));
        }
    }
}
