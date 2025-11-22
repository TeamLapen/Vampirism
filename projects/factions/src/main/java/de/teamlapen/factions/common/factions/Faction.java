package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private final int color;
    @NotNull
    private final IFactionVillage villageFactionData;
    @Nullable
    private String descriptionId;
    @Nullable
    private String descriptionIdPlural;
    @NotNull
    private final TextColor chatColor;
    private final Map<ResourceKey<? extends Registry<?>>, TagKey<?>> factionTags;

    Faction(FactionBuilder<T> builder) {
        this.color = builder.color;
        this.villageFactionData = builder.villageFactionData;
        this.chatColor = builder.chatColor == null ? TextColor.fromRgb(this.color) : builder.chatColor;
        this.factionTags = Collections.unmodifiableMap(builder.factionTags);
    }

    @Override
    public TextColor getChatColor() {
        return this.chatColor;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            return Util.makeDescriptionId("faction", RegUtil.id(this));
        }
        return this.descriptionId;
    }

    @Override
    public String getDescriptionIdPlural() {
        if (this.descriptionIdPlural == null) {
            this.descriptionIdPlural = getDescriptionId() + ".plural";
        }
        return this.descriptionIdPlural;
    }

    @Override
    public IFactionVillage getVillageData() {
        return villageFactionData;
    }

    @Override
    public <Z> Optional<TagKey<Z>> getTag(ResourceKey<? extends Registry<Z>> registryKey) {
        //noinspection unchecked
        return (Optional<TagKey<Z>>) (Object) Optional.ofNullable(factionTags.get(registryKey));
    }
}
