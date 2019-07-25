package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private static int nextId = 0;
    private final Class<T> entityInterface;
    private final int color;
    protected final ResourceLocation id;
    @Nullable
    private String translationKey;
    @Nullable
    private String translationKeyPlural;
    /**
     * Id used for hashing
     */
    private int integerId;
    private TextFormatting chatColor;

    Faction(ResourceLocation id, Class<T> entityInterface, int color) {
        this.id = id;
        this.entityInterface = entityInterface;
        this.color = color;
        integerId = nextId++;
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
    public ResourceLocation getID() {
        return id;
    }

    @Override
    public ITextComponent getName() {
        return translationKey == null ? new StringTextComponent(id.toString()) : new TranslationTextComponent(translationKey);
    }

    @Override
    public ITextComponent getNamePlural() {
        return translationKeyPlural == null ? new StringTextComponent(id.toString()) : new TranslationTextComponent(translationKeyPlural);
    }

    @Override
    public int hashCode() {
        return integerId;
    }

    @Override
    public boolean isEntityOfFaction(CreatureEntity creature) {
        return entityInterface.isInstance(creature);
    }


    public Faction<T> setTranslationKeys(String unlocalizedName, String unlocalizedNamePlural) {
        this.translationKey = unlocalizedName;
        this.translationKeyPlural = unlocalizedNamePlural;
        return this;
    }

    @Override
    public String toString() {
        return "Faction{" +
                "id='" + integerId + '\'' +
                ", hash=" + integerId +
                '}';
    }
}
