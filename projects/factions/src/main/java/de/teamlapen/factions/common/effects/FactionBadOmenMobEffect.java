package de.teamlapen.factions.common.effects;

import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.common.core.FactionDataComponents;
import de.teamlapen.factions.common.util.TotemHelper;
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
import org.jetbrains.annotations.Nullable;

public class FactionBadOmenMobEffect extends MobEffect {

    private final Holder<? extends IFaction<?>> faction;

    public FactionBadOmenMobEffect(Holder<? extends IFaction<?>> faction) {
        super(MobEffectCategory.NEUTRAL, 745784);
        this.faction = faction;
    }

    /**
     * Call this if onDeath of an entity that might carry a faction banner.
     * Checks if banner is equipped and handles bad omen effect accordingly
     *
     * @param offender Killer entity
     * @param victim   The killed faction entity
     */
    public static void handlePotentialBannerKill(@Nullable Entity offender, IFactionEntity victim) {
        if (offender instanceof Player player) {
            Holder<? extends IFaction<?>> faction = victim.getFaction();
            if (victim.asEntity().getItemBySlot(EquipmentSlot.HEAD).has(FactionDataComponents.IS_FACTION_BANNER)) {
                Holder<? extends IPlayableFaction<?>> playerFaction = FactionsMod.services().factionRegistry().getFaction(player);
                if (playerFaction != faction) {
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

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer && !entity.isSpectator()) {
            if (level.getDifficulty() == Difficulty.PEACEFUL) {
                return true;
            }
            return !TotemHelper.getTotemNearPos(level, entity.blockPosition(), true).filter(s -> !IFaction.is(s.getControllingFaction(), this.faction)).map(totem ->
                    totem.initiateCaptureOrIncreaseBadOmenLevel(this.faction, null, Math.min(amplifier, 4) + 1, 0)).orElse(false);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
