package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private static int nextId = 0;
    protected final ResourceLocation id;
    private final Class<T> entityInterface;
    private final int color;
    private final boolean hostileTowardsNeutral;
    /**
     * ID used for hashing
     */
    private final int integerId;
    @Nonnull
    private final IVillageFactionData villageFactionData;
    @Nullable
    private String translationKey;
    @Nullable
    private String translationKeyPlural;
    private ChatFormatting chatColor;

    Faction(ResourceLocation id, Class<T> entityInterface, int color, boolean hostileTowardsNeutral, @Nonnull IVillageFactionData villageFactionData) {
        this.id = id;
        this.entityInterface = entityInterface;
        this.color = color;
        this.hostileTowardsNeutral = hostileTowardsNeutral;
        this.villageFactionData = villageFactionData;
        integerId = nextId++;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Faction) && this.id == (((Faction<?>) obj).id);
    }

    @Override
    public ChatFormatting getChatColor() {
        return chatColor == null ? ChatFormatting.WHITE : chatColor;
    }

    public Faction<T> setChatColor(ChatFormatting chatColor) {
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
    public Component getName() {
        return translationKey == null ? new TextComponent(id.toString()) : new TranslatableComponent(translationKey);
    }

    @Override
    public Component getNamePlural() {
        return translationKeyPlural == null ? new TextComponent(id.toString()) : new TranslatableComponent(translationKeyPlural);
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
    public boolean isEntityOfFaction(PathfinderMob creature) {
        return entityInterface.isInstance(creature);
    }

    @Override
    public boolean isHostileTowardsNeutral() {
        return hostileTowardsNeutral;
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
