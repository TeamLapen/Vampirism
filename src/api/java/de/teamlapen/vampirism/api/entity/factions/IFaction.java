package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
    int getColor();

    /**
     * @return The interface all entities of this faction implement (or for players the IExtendedEntityProperty) implements
     */
    Class<T> getFactionEntityInterface();

    /**
     * If set returns unlocalized name. Otherwise returns {@link IFaction#name()}
     */
    String getTranslationKey();

    /**
     * If set returns unlocalized name in the plural form. Otherwise returns {@link IFaction#name()}
     */
    String getTranslationKeyPlural();

    /**
     * @return Translation component of name in singular
     */
    default ITextComponent getName() {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * @return Translation component of name in plural
     */
    default ITextComponent getNamePlural() {
        return new TranslationTextComponent(getTranslationKeyPlural());
    }

    boolean isEntityOfFaction(CreatureEntity creature);

    String name();

    /**
     * Set the unlocalized name
     *
     * @param name
     * @return The same instance
     */
    IFaction<T> setTranslationKeys(String name, String plural);
}
