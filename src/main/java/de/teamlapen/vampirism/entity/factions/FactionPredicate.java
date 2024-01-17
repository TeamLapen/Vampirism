package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.entity.factions.IDisguise;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Predicate for faction related selection
 */
public class FactionPredicate implements Predicate<LivingEntity> {
    private final @NotNull IFaction<?> thisFaction;
    private final boolean player;
    private final boolean nonPlayer;
    private final boolean neutralPlayer;
    private final boolean ignoreDisguise;
    /**
     * If null, all other faction are seen as hostile
     */
    @Nullable
    private final IFaction<?> otherFaction;

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
    protected FactionPredicate(@NotNull IFaction<?> thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction<?> otherFaction) {
        this.thisFaction = thisFaction;
        this.player = player;
        this.nonPlayer = nonPlayer;
        this.neutralPlayer = neutralPlayer;
        this.otherFaction = otherFaction;
        this.ignoreDisguise = ignoreDisguise;
    }

    protected FactionPredicate(@NotNull Faction<?> thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, boolean ignoreDisguise) {
        this(thisFaction, player, nonPlayer, neutralPlayer, ignoreDisguise, null);
    }

    @Override
    public boolean apply(@Nullable LivingEntity input) {
        if (input == null) return false;
        if (nonPlayer && input instanceof IFactionEntity) {
            IFaction<?> other = ((IFactionEntity) input).getFaction();
            return !thisFaction.equals(other) && (otherFaction == null || otherFaction.equals(other));

        }
        if (player && input instanceof Player && input.isAlive()) {
            return FactionPlayerHandler.getCurrentFactionPlayer((Player) input).map(fp -> {
                        IFaction<?> f = fp.getDisguise().getViewedFaction(thisFaction, ignoreDisguise);
                        return (f != null || (thisFaction.isHostileTowardsNeutral() && neutralPlayer)) && !thisFaction.equals(f) && (otherFaction == null || otherFaction.equals(f));
                    }
            ).orElse(neutralPlayer);
        }
        return false;
    }

    @Override
    public @NotNull String toString() {
        return "PredicateFaction{" +
                "thisFaction=" + thisFaction +
                ", player=" + player +
                ", nonPlayer=" + nonPlayer +
                ", neutralPlayer=" + neutralPlayer +
                ", otherFaction=" + otherFaction +
                '}';
    }
}
