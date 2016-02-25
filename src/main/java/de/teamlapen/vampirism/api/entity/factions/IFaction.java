package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.EnumChatFormatting;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

    /**
     * If not set returns white
     *
     * @return
     */
    EnumChatFormatting getChatColor();

    /**
     * Set the chat color
     *
     * @return The same instance
     */
    IFaction<T> setChatColor(EnumChatFormatting color);

    /**
     * Used for some rendering, e.g. for displaying the level
     */
    int getColor();

    /**
     * @return The interface all entities of this faction implement (or for players the IExtendedEntityProperty) implements
     */
    Class<T> getEntityInterface();

    /**
     * If set returns unlocalized name. Otherwise returns {@link IFaction#name()}
     *
     * @return
     */
    String getUnlocalizedName();

    /**
     * Set the unlocalized name
     *
     * @param name
     * @return The same instance
     */
    IFaction<T> setUnlocalizedName(String name);

    boolean isEntityOfFaction(EntityCreature creature);

    String name();
}
