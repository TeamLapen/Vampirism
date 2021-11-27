package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private static int nextId = 0;
    protected final ResourceLocation id;
    private final Class<T> entityInterface;
    private final Color color;
    private final boolean hostileTowardsNeutral;
    /**
     * Id used for hashing
     */
    private final int integerId;
    @Nonnull
    private IVillageFactionData villageFactionData;
    @Nonnull
    private ITextComponent name;
    @Nonnull
    private ITextComponent namePlural;
    @Nonnull
    private TextFormatting chatColor;

    Faction(ResourceLocation id, Class<T> entityInterface, Color color, boolean hostileTowardsNeutral, @Nonnull IVillageFactionData villageFactionData, @Nonnull TextFormatting chatColor, @Nonnull ITextComponent name, @Nonnull ITextComponent namePlural) {
        this.id = id;
        this.entityInterface = entityInterface;
        this.color = color;
        this.hostileTowardsNeutral = hostileTowardsNeutral;
        this.villageFactionData = villageFactionData;
        this.chatColor = chatColor;
        this.name = name;
        this.namePlural = namePlural;
        integerId = nextId++;
    }

    void finish(){
        this.villageFactionData = this.villageFactionData.build();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Faction) && this.id == (((Faction) obj).id);
    }

    @Nonnull
    @Override
    public TextFormatting getChatColor() {
        return this.chatColor;
    }

    @Override
    public Faction<T> setChatColor(TextFormatting chatColor) {
        this.chatColor = chatColor;
        return this;
    }

    @Override
    public Color getColor() {
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
        return name;
    }

    @Override
    public ITextComponent getNamePlural() {
        return namePlural;
    }

    @Nonnull
    @Override
    public IVillageFactionData getVillageData() {
        return villageFactionData;
    }

    @Override
    public int hashCode() {
        return integerId;
    }

    @Override
    public boolean isEntityOfFaction(CreatureEntity creature) {
        return entityInterface.isInstance(creature);
    }

    @Override
    public boolean isHostileTowardsNeutral() {
        return hostileTowardsNeutral;
    }

    @Override
    public Faction<T> setTranslationKeys(String unlocalizedName, String unlocalizedNamePlural) {
        this.name = new TranslationTextComponent(unlocalizedName);
        this.namePlural = new TranslationTextComponent(unlocalizedNamePlural);
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
