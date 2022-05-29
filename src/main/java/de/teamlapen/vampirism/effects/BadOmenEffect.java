package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * does not extends {@link VampirismEffect} so other mods can use this too
 */
public abstract class BadOmenEffect extends Effect {

    /**
     * Call this if onDeath of an entity that might carry a faction banner.
     * Checks if banner is equipped and handles bad omen effect accordingly
     *
     * @param offender Killer entity
     * @param victim   The killed faction entity
     */
    public static void handlePotentialBannerKill(@Nullable Entity offender, IFactionEntity victim) {
        if (offender instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) offender;
            IFaction<?> faction = victim.getFaction();
            if (faction.getVillageData().isBanner(victim.getRepresentingEntity().getItemBySlot(EquipmentSlotType.HEAD))) {
                IFaction<?> playerFaction = VampirismPlayerAttributes.get(player).faction;
                if (playerFaction != null && playerFaction != faction) {
                    Effect badOmen = faction.getVillageData().getBadOmenEffect();
                    if (badOmen != null) {
                        EffectInstance inst = player.getEffect(badOmen);
                        int i = inst != null ? Math.min(inst.getAmplifier() + 1, 4) : 0;
                        if (inst != null) player.removeEffectNoUpdate(badOmen);
                        player.addEffect(new EffectInstance(badOmen, 120000, i, false, false, true));
                    }

                }
            }
        }
    }

    public BadOmenEffect() {
        super(EffectType.NEUTRAL, 745784);
    }

    public abstract IFaction<?> getFaction();

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof ServerPlayerEntity && !entityLivingBaseIn.isSpectator()) {
            ServerPlayerEntity playerEntity = ((ServerPlayerEntity) entityLivingBaseIn);
            ServerWorld serverWorld = playerEntity.getLevel();
            if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            TotemHelper.getTotemNearPos(serverWorld, entityLivingBaseIn.blockPosition(), true).ifPresent(totem -> {
                if (totem.getControllingFaction() != getFaction()) {
                    int level = Math.min(amplifier, 4);
                    if (totem.initiateCaptureOrIncreaseBadOmenLevel(getFaction(), null, level + 1, 0)) {
                        entityLivingBaseIn.removeEffect(this);
                    }
                }
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
