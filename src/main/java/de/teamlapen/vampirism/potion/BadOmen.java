package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

/**
 * does not extends {@link VampirismEffect} so other mods can use this too
 */
public abstract class BadOmen extends Effect {

    public BadOmen(String modID, ResourceLocation faction) {
        super(EffectType.NEUTRAL, 745784);
        this.setRegistryName(modID, "bad_omen_" + faction.getPath());
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    public abstract IFaction<?> getFaction();

    @Override
    public void performEffect(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof ServerPlayerEntity && !entityLivingBaseIn.isSpectator()) {
            ServerPlayerEntity playerEntity = ((ServerPlayerEntity) entityLivingBaseIn);
            ServerWorld serverWorld = playerEntity.getServerWorld();
            if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            TotemHelper.getTotemNearPos(serverWorld, entityLivingBaseIn.getPosition(), true).ifPresent(totem -> {
                if (totem.getControllingFaction() != getFaction()) {
                    int level = Math.min(amplifier, 4);
                    totem.initiateCapture(getFaction(), null, level + 1, 0.25f + 0.4375f * level);
                    entityLivingBaseIn.removePotionEffect(this);
                }
            });
        }
    }
}
