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
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof ServerPlayerEntity && !entityLivingBaseIn.isSpectator()) {
            ServerPlayerEntity playerEntity = ((ServerPlayerEntity) entityLivingBaseIn);
            ServerWorld serverWorld = playerEntity.getServerWorld();
            if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            TotemHelper.getTotemNearPos(serverWorld, entityLivingBaseIn.getPosition(),true).ifPresent(totem -> {
                if (totem.getControllingFaction() != getFaction()) {
                    totem.initiateCapture(getFaction(),0.7f, null, true);
                    entityLivingBaseIn.removePotionEffect(this);
                }
            });
        }
    }
}
