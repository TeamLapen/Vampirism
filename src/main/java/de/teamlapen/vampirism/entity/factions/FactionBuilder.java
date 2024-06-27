package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FactionBuilder<T extends IFactionEntity> implements IFactionBuilder<T> {

    protected int color = Color.WHITE.getRGB();
    protected IFactionVillage villageFactionData;
    protected @Nullable TextColor chatColor;
    protected String name;
    protected String namePlural;
    protected Map<ResourceKey<? extends Registry<?>>, TagKey<?>> factionTags = new HashMap<>();

    @Override
    public IFactionBuilder<T> color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public IFactionBuilder<T> chatColor(TextColor color) {
        this.chatColor = color;
        return this;
    }

    @Override
    public IFactionBuilder<T> chatColor(@NotNull ChatFormatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("Parameter must be a color");
        }
        this.chatColor = TextColor.fromLegacyFormat(color);
        return this;
    }

    @Override
    public IFactionBuilder<T> village(@NotNull IFactionVillage villageBuilder) {
        this.villageFactionData = villageBuilder;
        return this;
    }

    @Override
    public IFactionBuilder<T> name(@NotNull String nameKey) {
        this.name = nameKey;
        return this;
    }

    @Override
    public IFactionBuilder<T> namePlural(@NotNull String namePluralKey) {
        this.namePlural = namePluralKey;
        return this;
    }

    @Override
    public @NotNull IFaction<T> build() {
        return new Faction<>(this);
    }

    @Override
    public <Z> IFactionBuilder<T> addTag(ResourceKey<? extends Registry<Z>> registryKey, TagKey<Z> tag) {
        this.factionTags.put(registryKey, tag);
        return this;
    }
}
