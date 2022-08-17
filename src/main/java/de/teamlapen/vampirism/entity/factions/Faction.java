package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private final IFactionVillage villageFactionData;
    @NotNull
    private final Component name;
    @NotNull
    private final Component namePlural;
    @NotNull
    private final TextColor chatColor;

    Faction(FactionRegistry.FactionBuilder<T> builder) {
        this.id = builder.id;
        this.entityInterface = builder.entityInterface;
        this.color = builder.color;
        this.hostileTowardsNeutral = builder.hostileTowardsNeutral;
        this.villageFactionData = builder.villageFactionData.build();
        this.chatColor = builder.chatColor == null ? TextColor.fromRgb(this.color) : builder.chatColor;
        this.name = builder.name == null ? Component.literal(id.toString()) : Component.translatable(builder.name);
        this.namePlural = builder.namePlural == null ? this.name : Component.translatable(builder.namePlural);
        integerId = nextId++;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Faction) && this.id == (((Faction<?>) obj).id);
    }

    @NotNull
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

    @NotNull
    @Override
    public Component getName() {
        return name;
    }

    @NotNull
    @Override
    public Component getNamePlural() {
        return namePlural;
    }

    @NotNull
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
