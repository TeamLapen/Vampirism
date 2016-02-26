package de.teamlapen.vampirism.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * Predicate for faction related selection
 */
public class PredicateFaction implements Predicate<Entity> {
    private final IFaction thisFaction;
    private final boolean player;
    private final boolean nonPlayer;
    private final boolean neutralPlayer;
    /**
     * If null, all other faction are seen as hostile
     */
    private final IFaction otherFaction;

    /**
     * Selects entities
     *
     * @param thisFaction   The friendly faction
     * @param player        If players should be selected
     * @param nonPlayer     If non players should be selected
     * @param neutralPlayer If neutral playsers should be selected
     * @param otherFaction  If this is not null, only entities of this faction are selected.
     */
    PredicateFaction(IFaction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, IFaction otherFaction) {
        this.thisFaction = thisFaction;
        this.player = player;
        this.nonPlayer = nonPlayer;
        this.neutralPlayer = neutralPlayer;
        this.otherFaction = otherFaction;
    }

    PredicateFaction(Faction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer) {
        this(thisFaction, player, nonPlayer, neutralPlayer, null);
    }

    @Override
    public boolean apply(@Nullable Entity input) {
        if (input == null || !(input instanceof EntityLivingBase)) return false;
        if (nonPlayer && input instanceof IFactionEntity) {
            IFaction other = ((IFactionEntity) input).getFaction();
            return !thisFaction.equals(other) && (otherFaction == null || otherFaction.equals(other));

        }
        if (player && input instanceof EntityPlayer) {
            IFaction f = VampirismAPI.getFactionPlayerHandler((EntityPlayer) input).getCurrentFaction();
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
