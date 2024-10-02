package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private final int color;
    @NotNull
    private final IFactionVillage villageFactionData;
    @NotNull
    private final Component name;
    @NotNull
    private final Component namePlural;
    @NotNull
    private final TextColor chatColor;

    Faction(FactionBuilder<T> builder) {
        this.color = builder.color;
        this.villageFactionData = builder.villageFactionData;
        this.chatColor = builder.chatColor == null ? TextColor.fromRgb(this.color) : builder.chatColor;
        this.name = Component.translatable(Objects.requireNonNull(builder.name));
        this.namePlural = Component.translatable(Objects.requireNonNull(builder.namePlural));
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
    public Component getName() {
        return name;
    }

    @Override
    public Component getNamePlural() {
        return namePlural;
    }

    @Override
    public IFactionVillage getVillageData() {
        return villageFactionData;
    }

    @Override
    public <Z> Optional<TagKey<Z>> getTag(ResourceKey<Z> registryKey) {
        return FactionTags.getTag(this, registryKey);
    }

    @Override
    public <Z> Optional<TagKey<Z>> getRegistryTag(ResourceKey<? extends Registry<Z>> registryKey) {
        return FactionTags.getRegistryTag(this, registryKey);
    }
}
