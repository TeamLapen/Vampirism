package de.teamlapen.vampirism.api.entity.factions;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.EntityLivingBase;

/**
 * Faction registry.
 * Register all extended properties that extend {@link IFactionPlayer} here
 * Currently only used for managing IPlayerEventListeners.
 */
public interface IFactionRegistry {


    /**
     * @return All factions after post init
     */
    IFaction[] getFactions();

    /**
     * @return All playable factions after post init
     */
    IPlayableFaction[] getPlayableFactions();

    /**
     * Get a cached or create a predicate which selects entities from other factions
     * @param thisFaction   The friendly faction
     * @param player        If players should be selected
     * @param mob     If non players should be selected
     * @param neutralPlayer If neutral playsers should be selected
     * @param otherFaction  If this is not null, only entities of this faction are selected.
     * @return
     */
    Predicate<EntityLivingBase> getPredicate(IFaction thisFaction, boolean player, boolean mob, boolean neutralPlayer, IFaction otherFaction);

    /**
     * Get a cached or create a predicate which selects all other faction entities
     * @param thisFaction
     * @return
     */
    Predicate<EntityLivingBase> getPredicate(IFaction thisFaction);

    <T extends IFactionEntity> IFaction registerFaction(String name, Class<T> entityInterface, int color);

    <T extends IFactionPlayer> IPlayableFaction registerPlayableFaction(String name, Class<T> entityInterface, int color, String playerProp, int highestLevel);
}
