package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

    /**
     * If not set returns white
     */
    TextFormatting getChatColor();

    /**
     * Set the chat color
     *
     * @return The same instance
     */
    IFaction<T> setChatColor(TextFormatting color);

    /**
     * Used for some rendering, e.g. for displaying the level
     */
    Color getColor();

    /**
     * @return The interface all entities of this faction implement (or for players the IExtendedEntityProperty) implements
     */
    Class<T> getFactionEntityInterface();


    /**
     * @return Unique key of this faction
     */
    ResourceLocation getID();

    ITextComponent getName();


    boolean isEntityOfFaction(CreatureEntity creature);

    /**
     * Preferably a TextComponentTranslation
     */
    ITextComponent getNamePlural();


    /**
     * Set the unlocalized name
     *
     * @param name
     * @return The same instance
     */
    IFaction<T> setTranslationKeys(String name, String plural);

    /**
     * @return Whether entities of this faction are hostile towards neutral entities
     */
    boolean isHostileTowardsNeutral();
}
