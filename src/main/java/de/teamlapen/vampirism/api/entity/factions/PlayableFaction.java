package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public abstract class PlayableFaction<T extends IFactionPlayer> extends Faction {
    public final String prop;


    /**
     * @param name  Name
     * @param prop  ExtendedPlayerPropertiesKey
     * @param iface Interface each entity (or for playable factions the IExtendedEntityProperties) implements
     */
    public PlayableFaction(String name, Class<? extends IFactionEntity> iface, String prop) {
        super(name, iface);
        this.prop = prop;
    }

    public abstract int getHighestReachableLevel();

    public T getProp(EntityPlayer player) {
        return (T) player.getExtendedProperties(prop);
    }


}
