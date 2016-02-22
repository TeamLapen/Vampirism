package de.teamlapen.vampirism.api.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * Created by Max on 22.02.2016.
 */
public class PredicateFactionHostile implements Predicate<EntityLivingBase> {
    private final Faction thisFaction;
    private final boolean player;
    private final boolean nonPlayer;
    private final boolean neutralPlayer;

    public PredicateFactionHostile(Faction thisFaction, boolean player, boolean nonPlayer, boolean neutralPlayer) {
        this.thisFaction = thisFaction;
        this.player = player;
        this.nonPlayer = nonPlayer;
        this.neutralPlayer = neutralPlayer;
    }

    @Override
    public boolean apply(@Nullable EntityLivingBase input) {
        if (input == null) return false;
        if (nonPlayer && input instanceof IFactionEntity) {
            return !thisFaction.equals(((IFactionEntity) input).getFaction());

        }
        if (player && input instanceof EntityPlayer) {
            Faction f = VampirismAPI.getFactionPlayerHandler((EntityPlayer) input).getCurrentFaction();
            if (f == null) {
                return neutralPlayer;
            } else {
                return !thisFaction.equals(f);
            }
        }
        return false;
    }
}
