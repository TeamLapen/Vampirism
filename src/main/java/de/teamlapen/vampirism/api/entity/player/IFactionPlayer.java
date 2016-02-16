package de.teamlapen.vampirism.api.entity.player;

import com.google.common.base.Predicate;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Basic interface for all of Vampirism's player types (VampirePlayer, HunterPlayer, ...)
 * The player can have levels.
 * A player can only be part of one faction at once, this means only one IFaction ExtendedProperties belonging to a single player can have a level >0.
 */
public interface IFactionPlayer extends IFactionEntity, IExtendedEntityProperties {
    /**
     * @return 0 if the player is not part of this faction, something > 0 if the player is part of the faction.
     */
    int getLevel();

    /**
     * Sets the vampire level.
     * TODO check that it is a area check exists
     * @param level
     */
    void setLevel(int level);

    EntityPlayer getRepresentingPlayer();

    void levelUp();

    /**
     * @return the faction this faction player belongs to
     */
    PlayableFaction<? extends IFactionPlayer> getFaction();

    /**
     * @param otherFactionPlayers Whether other entities from the same faction that might be hostile should be included
     * @return A predicate that selects all non friendly entities
     */
    Predicate<? super Entity> getNonFriendlySelector(boolean otherFactionPlayers);



}
