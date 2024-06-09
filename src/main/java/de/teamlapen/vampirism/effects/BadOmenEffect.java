package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.TotemHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * does not extend {@link VampirismEffect} so other mods can use this too
 */
public class BadOmenEffect extends MobEffect {

    private final Holder<? extends IFaction<?>> faction;

    /**
     * Call this if onDeath of an entity that might carry a faction banner.
     * Checks if banner is equipped and handles bad omen effect accordingly
     *
     * @param offender Killer entity
     * @param victim   The killed faction entity
     */
    public static void handlePotentialBannerKill(@Nullable Entity offender, @NotNull IFactionEntity victim) {
        if (offender instanceof Player player) {
            Holder<? extends IFaction<?>> faction = victim.getFaction();
            if (faction.value().getVillageData().isBanner(victim.asEntity().getItemBySlot(EquipmentSlot.HEAD), offender.registryAccess())) {
                Holder<? extends IPlayableFaction<?>> playerFaction = VampirismPlayerAttributes.get(player).faction;
                if (playerFaction != null && playerFaction != faction) {
                    Holder<MobEffect> badOmen = faction.value().getVillageData().badOmenEffect();
                    if (badOmen != null) {
                        MobEffectInstance inst = player.getEffect(badOmen);
                        int i = inst != null ? Math.min(inst.getAmplifier() + 1, 4) : 0;
                        if (inst != null) player.removeEffectNoUpdate(badOmen);
                        player.addEffect(new MobEffectInstance(badOmen, 120000, i, false, false, true));
                    }

                }
            }
        }
    }

    public BadOmenEffect(Holder<? extends IFaction<?>> faction) {
        super(MobEffectCategory.NEUTRAL, 745784);
        this.faction = faction;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof ServerPlayer playerEntity && !entityLivingBaseIn.isSpectator()) {
            ServerLevel serverWorld = playerEntity.serverLevel();
            if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
                return true;
            }
            return !TotemHelper.getTotemNearPos(serverWorld, entityLivingBaseIn.blockPosition(), true).filter(s -> !IFaction.is(s.getControllingFaction(), this.faction)).map(totem -> {
                return totem.initiateCaptureOrIncreaseBadOmenLevel(this.faction, null, Math.min(amplifier, 4) + 1, 0);
            }).orElse(false);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int p_295329_, int p_295167_) {
        return true;
    }
}
