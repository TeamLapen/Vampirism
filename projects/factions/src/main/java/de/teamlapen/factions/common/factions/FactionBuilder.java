package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionBuilder;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FactionBuilder<T extends IFactionEntity> implements IFactionBuilder<T> {

    protected int color = ARGB.white(1);
    protected IFactionVillage villageFactionData;
    protected @Nullable TextColor chatColor;
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
    public IFactionBuilder<T> chatColor(ChatFormatting color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException("Parameter must be a color");
        }
        this.chatColor = TextColor.fromLegacyFormat(color);
        return this;
    }

    @Override
    public IFactionBuilder<T> village(IFactionVillage villageBuilder) {
        this.villageFactionData = villageBuilder;
        return this;
    }

    @Override
    public IFaction<T> build() {
        return new Faction<>(this);
    }

    @Override
    public <Z> IFactionBuilder<T> addTag(ResourceKey<? extends Registry<Z>> registryKey, TagKey<Z> tag) {
        this.factionTags.put(registryKey, tag);
        return this;
    }
}
