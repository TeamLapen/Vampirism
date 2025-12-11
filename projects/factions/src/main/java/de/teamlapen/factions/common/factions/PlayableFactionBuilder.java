package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IPlayableFactionBuilder;
import de.teamlapen.factions.api.factions.lord.ILordPlayerBuilder;
import de.teamlapen.factions.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.factions.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlayableFactionBuilder<T extends IFactionPlayer<T>> extends FactionBuilder<T> implements IPlayableFactionBuilder<T> {

    protected final Supplier<AttachmentType<T>> playerCapabilitySupplier;
    protected int highestLevel = 1;
    protected Map<IRefinementItem.AccessorySlotType, List<Supplier<IRefinementItem>>> refinementItemBySlot = new HashMap<>();
    @Nullable
    protected ILordPlayerEntry lord;

    public PlayableFactionBuilder(Supplier<AttachmentType<T>> playerCapabilitySupplier) {
        this.playerCapabilitySupplier = playerCapabilitySupplier;
    }

    @Override
    public PlayableFactionBuilder<T> color(int color) {
        return (PlayableFactionBuilder<T>) super.color(color);
    }

    @Override
    public PlayableFactionBuilder<T> highestLevel(int highestLevel) {
        this.highestLevel = highestLevel;
        return this;
    }

    @Override
    public PlayableFactionBuilder<T> village(IFactionVillage villageBuilder) {
        return (PlayableFactionBuilder<T>) super.village(villageBuilder);
    }

    @Override
    public PlayableFactionBuilder<T> village(Consumer<IFactionVillageBuilder> villageBuilder) {
        return (PlayableFactionBuilder<T>) super.village(villageBuilder);
    }

    @Override
    public PlayableFactionBuilder<T> refinementItem(IRefinementItem.AccessorySlotType type, Supplier<IRefinementItem> item) {
        this.refinementItemBySlot.computeIfAbsent(type, t -> new ArrayList<>()).add(item);
        return this;
    }

    @Override
    public PlayableFactionBuilder<T> chatColor(TextColor color) {
        return (PlayableFactionBuilder<T>) super.chatColor(color);
    }

    @Override
    public PlayableFactionBuilder<T> chatColor(ChatFormatting color) {
        return (PlayableFactionBuilder<T>) super.chatColor(color);
    }

    @Override
    public PlayableFactionBuilder<T> lord(ILordPlayerEntry builder) {
        this.lord = builder;
        return this;
    }

    @Override
    public PlayableFactionBuilder<T> lord(Consumer<ILordPlayerBuilder<T>> builder) {
        var entry = new LordPlayerBuilder<T>();
        builder.accept(entry);
        this.lord = entry.build();
        return this;
    }

    @Override
    public <Z> PlayableFactionBuilder<T> addTag(ResourceKey<? extends Registry<Z>> registryKey, TagKey<Z> tag) {
        return (PlayableFactionBuilder<T>) super.addTag(registryKey, tag);
    }

    @Override
    public PlayableFaction<T> build() {
        return new PlayableFaction<>(this);
    }
}
