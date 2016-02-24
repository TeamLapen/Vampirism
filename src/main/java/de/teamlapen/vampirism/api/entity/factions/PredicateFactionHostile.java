package de.teamlapen.vampirism.api.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;


public class PredicateFactionHostile implements Predicate<EntityLivingBase> {
    private final Faction thisFaction;
    private final boolean player;
    private final boolean nonPlayer;
    private final boolean neutralPlayer;
    /**
     * If null, all other faction are seen as hostile
     */
    private final Faction otherFaction;

    /**
     * Selects entities
     *
     * @param thisFaction   The friendly faction
     * @param player        If players should be selected
     * @param nonPlayer     If non players should be selected
     * @param neutralPlayer If neutral playsers should be selected
     * @param otherFaction  If this is not null, only entities of this faction are selected.
     */
    public PredicateFactionHostile(Faction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer, Faction otherFaction) {
        this.thisFaction = thisFaction;
        this.player = player;
        this.nonPlayer = nonPlayer;
        this.neutralPlayer = neutralPlayer;
        this.otherFaction= otherFaction;
    }

    public PredicateFactionHostile(Faction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer) {
        this(thisFaction, player, nonPlayer, neutralPlayer,null);
    }

    @Override
    public boolean apply(@Nullable EntityLivingBase input) {
        if (input == null) return false;
        if (nonPlayer && input instanceof IFactionEntity) {
            Faction other = ((IFactionEntity) input).getFaction();
            return !thisFaction.equals(other) && (otherFaction == null || otherFaction.equals(other));

        }
        if (player && input instanceof EntityPlayer) {
            Faction f = VampirismAPI.getFactionPlayerHandler((EntityPlayer) input).getCurrentFaction();
            if (f == null) {
                return neutralPlayer;
            } else {
                return !thisFaction.equals(f) && (otherFaction == null || otherFaction.equals(f));
            }
        }
        return false;
    }
}
