package de.teamlapen.vampirism.api.entity.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

    Codec<IFaction<?>> CODEC = RecordCodecBuilder.create(ins -> ins.group(ResourceLocation.CODEC.fieldOf("id").forGetter(IFaction::getID)).apply(ins, (id) -> VampirismAPI.factionRegistry().getFactionByID(id)));

    /**
     * If not set returns white
     */
    TextColor getChatColor();

    /**
     * Used for some rendering, e.g. for displaying the level
     */
    int getColor();

    /**
     * @return The interface all entities of this faction implement (or for players the IExtendedEntityProperty) implements
     */
    Class<T> getFactionEntityInterface();


    /**
     * @return Unique key of this faction
     */
    ResourceLocation getID();

    Component getName();

    /**
     * Preferably a TextComponentTranslation
     */
    Component getNamePlural();

    /**
     * Gets Village Totem related utility class
     *
     * @return the village data class
     */
    @NotNull
    IFactionVillage getVillageData();

    boolean isEntityOfFaction(PathfinderMob creature);

    /**
     * @return Whether entities of this faction are hostile towards neutral entities
     */
    boolean isHostileTowardsNeutral();

    <Z> Optional<TagKey<Z>> getTag(ResourceKey<? extends Registry<Z>> registryKey);

}
