package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The fade envelope backing the aura of darkness, so it thins in and out rather than popping.
 * <p>
 * The envelope cannot be derived from the effect instance itself: {@link
 * de.teamlapen.vampirism.common.world.entity.player.vampire.actions.AuraOfDarknessAction} re-applies the effect
 * every 40 ticks with a 100-tick duration, so the remaining duration never ramps up and only ever ramps down once
 * the action has already ended.
 */
public class AuraOfDarknessStateTracker {

    private static final float FADE_TICKS = 8.0f;

    private final Map<UUID, FadeState> states = new HashMap<>();

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
            boolean affected = entity instanceof LivingEntity living && (living.hasEffect(ModEffects.AURA_OF_DARKNESS) || living.hasEffect(ModEffects.SUNSCREEN));
            FadeState state = this.states.get(entity.getUUID());
            if (state == null) {
                if (!affected) {
                    continue;
                }
                state = new FadeState();
                this.states.put(entity.getUUID(), state);
            }
            state.tick(affected);
        }

        // Anything that stopped rendering keeps its last state until it decays on its own, which is what lets the
        // aura fade out rather than vanish when its entity unloads.
        this.states.values().removeIf(state -> state.current <= 0.0f);
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        this.states.clear();
    }

    /**
     * @return 0 when no aura should be drawn for this entity, ramping to 1 while it carries the effect
     */
    public float getFade(Entity entity, float partialTick) {
        FadeState state = this.states.get(entity.getUUID());
        return state == null ? 0.0f : Mth.lerp(partialTick, state.previous, state.current);
    }

    private static class FadeState {
        private float previous;
        private float current;

        private void tick(boolean affected) {
            this.previous = this.current;
            this.current = Mth.clamp(this.current + (affected ? 1.0f : -1.0f) / FADE_TICKS, 0.0f, 1.0f);
        }
    }
}
