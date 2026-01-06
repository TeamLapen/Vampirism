package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.IPlayableFactionBuilder;
import de.teamlapen.faction.api.factions.lord.ILordPlayerBuilder;
import de.teamlapen.faction.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.faction.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
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
    protected final Map<IRefinementItem.AccessorySlotType, List<Supplier<IRefinementItem>>> refinementItemBySlot = new HashMap<>();
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
    public PlayableFactionBuilder<T> lord(Consumer<ILordPlayerBuilder<T>> builder) {
        var entry = new LordPlayerBuilder<T>();
        builder.accept(entry);
        this.lord = entry.build();
        return this;
    }

    @Override
    public PlayableFaction<T> build() {
        return new PlayableFaction<>(this);
    }
}
