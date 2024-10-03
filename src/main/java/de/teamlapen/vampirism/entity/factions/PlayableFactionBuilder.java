package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import de.teamlapen.vampirism.api.entity.factions.ILordPlayerEntry;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFactionBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PlayableFactionBuilder<T extends IFactionPlayer<T>> extends FactionBuilder<T> implements IPlayableFactionBuilder<T> {

    protected final Supplier<AttachmentType<T>> playerCapabilitySupplier;
    protected int highestLevel = 1;
    protected Map<IRefinementItem.AccessorySlotType, List<Supplier<IRefinementItem>>> refinementItemBySlot = new HashMap<>();
    protected ILordPlayerEntry lord;

    public PlayableFactionBuilder(Supplier<AttachmentType<T>> playerCapabilitySupplier) {
        this.playerCapabilitySupplier = playerCapabilitySupplier;
    }

    @Override
    public PlayableFactionBuilder<T> color(int color) {
        return (PlayableFactionBuilder<T>) super.color(color);
    }

    @Override
    public @NotNull PlayableFactionBuilder<T> highestLevel(int highestLevel) {
        this.highestLevel = highestLevel;
        return this;
    }

    @Override
    public PlayableFactionBuilder<T> village(@NotNull IFactionVillage villageBuilder) {
        return (PlayableFactionBuilder<T>) super.village(villageBuilder);
    }

    @Override
    public @NotNull PlayableFactionBuilder<T> refinementItem(@NotNull IRefinementItem.AccessorySlotType type, Supplier<IRefinementItem> item) {
        this.refinementItemBySlot.computeIfAbsent(type, t -> new ArrayList<>()).add(item);
        return this;
    }

    @Override
    public PlayableFactionBuilder<T> chatColor(@NotNull TextColor color) {
        return (PlayableFactionBuilder<T>) super.chatColor(color);
    }

    @Override
    public PlayableFactionBuilder<T> chatColor(@NotNull ChatFormatting color) {
        return (PlayableFactionBuilder<T>) super.chatColor(color);
    }

    @Override
    public PlayableFactionBuilder<T> name(@NotNull String nameKey) {
        return (PlayableFactionBuilder<T>) super.name(nameKey);
    }

    @Override
    public PlayableFactionBuilder<T> namePlural(@NotNull String namePluralKey) {
        return (PlayableFactionBuilder<T>) super.namePlural(namePluralKey);
    }

    @Override
    public @NotNull PlayableFactionBuilder<T> lord(ILordPlayerEntry builder) {
        this.lord = builder;
        return this;
    }

    @Override
    public <Z> PlayableFactionBuilder<T> addTag(ResourceKey<Z> key, TagKey<Z> tag) {
        return (PlayableFactionBuilder<T>) super.addTag(key, tag);
    }

    @Override
    public <Z> PlayableFactionBuilder<T> addRegistryTag(ResourceKey<? extends Registry<Z>> key, TagKey<Z> tag) {
        return (PlayableFactionBuilder<T>) super.addRegistryTag(key, tag);
    }

    @Override
    public @NotNull PlayableFaction<T> build() {
        var faction = new PlayableFaction<>(this);
        FactionTags.addFaction(faction, this.tags);
        return faction;
    }
}
