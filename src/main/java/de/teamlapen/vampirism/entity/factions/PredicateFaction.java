package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Predicate for faction related selection
 */
public class PredicateFaction implements Predicate<LivingEntity> {
    private final IFaction thisFaction;
    private final boolean player;
    private final boolean nonPlayer;
    private final boolean neutralPlayer;
    private final boolean ignoreDisguise;
    /**
     * If null, all other faction are seen as hostile
     */
    private final
    @Nullable
    IFaction otherFaction;

    /**
     * Selects entities
     *
     * @param thisFaction    The friendly faction
     * @param player         If players should be selected
     * @param nonPlayer      If non players should be selected
     * @param neutralPlayer  If neutral playsers should be selected
     * @param ignoreDisguise If the disguise ability of players should be ignored.
     * @param otherFaction   If this is not null, only entities of this faction are selected.
     */
    protected PredicateFaction(@Nonnull IFaction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction otherFaction) {
        this.thisFaction = thisFaction;
        this.player = player;
        this.nonPlayer = nonPlayer;
        this.neutralPlayer = neutralPlayer;
        this.otherFaction = otherFaction;
        this.ignoreDisguise = ignoreDisguise;
    }

    protected PredicateFaction(Faction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, boolean ignoreDisguise) {
        this(thisFaction, player, nonPlayer, neutralPlayer, ignoreDisguise, null);
    }

    @Override
    public boolean apply(@Nullable LivingEntity input) {
        if (input == null) return false;
        if (nonPlayer && input instanceof IFactionEntity) {
            IFaction other = ((IFactionEntity) input).getFaction();
            return !thisFaction.equals(other) && (otherFaction == null || otherFaction.equals(other));

        }
        if (player && input instanceof PlayerEntity && input.isAlive()) {
            IFactionPlayer fp = FactionPlayerHandler.get((PlayerEntity) input).getCurrentFactionPlayer().orElse(null);
            IFaction f = fp == null ? null : (ignoreDisguise ? fp.getFaction() : fp.getDisguisedAs());
            if (f == null) {
                return neutralPlayer;
            } else {
                return !thisFaction.equals(f) && (otherFaction == null || otherFaction.equals(f));
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PredicateFaction{" +
                "thisFaction=" + thisFaction +
                ", player=" + player +
                ", nonPlayer=" + nonPlayer +
                ", neutralPlayer=" + neutralPlayer +
                ", otherFaction=" + otherFaction +
                '}';
    }
}
