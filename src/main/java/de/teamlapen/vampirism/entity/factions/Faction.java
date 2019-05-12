package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.text.TextFormatting;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private static int nextId = 0;
    protected final String name;
    private final Class<T> entityInterface;
    private final int color;
    private String translationKey;
    private String translationKeyPlural;
    /**
     * Id used for hashing
     */
    private int id;
    private TextFormatting chatColor;

    Faction(String name, Class<T> entityInterface, int color) {
        this.name = name;
        this.entityInterface = entityInterface;
        this.color = color;
        id = nextId++;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Faction) && this.id == (((Faction) obj).id);
    }

    @Override
    public TextFormatting getChatColor() {
        return chatColor == null ? TextFormatting.WHITE : chatColor;
    }

    public Faction<T> setChatColor(TextFormatting chatColor) {
        this.chatColor = chatColor;
        return this;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public Class<T> getFactionEntityInterface() {
        return entityInterface;
    }

    @Override
    public String getTranslationKey() {
        return translationKey == null ? name : translationKey;
    }

    @Override
    public String getTranslationKeyPlural() {
        return translationKeyPlural == null ? name : translationKeyPlural;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean isEntityOfFaction(EntityCreature creature) {
        return entityInterface.isInstance(creature);
    }

    @Override
    public String name() {
        return name;
    }

    public Faction<T> setTranslationKeys(String unlocalizedName, String unlocalizedNamePlural) {
        this.translationKey = unlocalizedName;
        this.translationKeyPlural = unlocalizedNamePlural;
        return this;
    }

    @Override
    public String toString() {
        return "Faction{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    protected int getId() {
        return id;
    }
}
