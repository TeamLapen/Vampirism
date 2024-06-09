package de.teamlapen.vampirism.api.entity.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

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
     * @deprecated use HostileTowardsNeutral tag instead
     */
    @Deprecated
    boolean isHostileTowardsNeutral();

    <Z> Optional<TagKey<Z>> getTag(ResourceKey<? extends Registry<Z>> registryKey);

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    static boolean is(@Nullable Holder<? extends IFaction<?>> first, @Nullable Holder<? extends IFaction<?>> second) {
        if (first == null) {
            return second == null;
        }
        return second != null && first.is((Holder) second);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T extends IFaction<?>> boolean is(@Nullable Holder<? extends IFaction<?>> first, @Nullable TagKey<T> second) {
        if (first == null) {
            return second == null;
        }
        return second != null && first.is((TagKey) second);
    }

    static <T extends IFaction<?>, Z extends IFaction<?>> boolean is(TagKey<Z> first, TagKey<T> second) {
        return first.location().equals(second.location());
    }
}
