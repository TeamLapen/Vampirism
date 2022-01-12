package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

import javax.annotation.Nonnull;

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
    private IFactionVillage villageFactionData;
    @Nonnull
    private final Component name;
    @Nonnull
    private final Component namePlural;
    @Nonnull
    private final TextColor chatColor;

    Faction(ResourceLocation id, Class<T> entityInterface, int color, boolean hostileTowardsNeutral, @Nonnull IFactionVillage villageFactionData, @Nonnull TextColor chatColor, @Nonnull Component name, @Nonnull Component namePlural) {
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
        return (obj instanceof Faction) && this.id == (((Faction<?>) obj).id);
    }

    @Nonnull
    @Override
    public TextColor getChatColor() {
        return this.chatColor;
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

    @Nonnull
    @Override
    public Component getName() {
        return name;
    }

    @Nonnull
    @Override
    public Component getNamePlural() {
        return namePlural;
    }

    @Nonnull
    @Override
    public IFactionVillage getVillageData() {
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

    @Override
    public String toString() {
        return "Faction{" +
                "id='" + integerId + '\'' +
                ", hash=" + integerId +
                '}';
    }
}
