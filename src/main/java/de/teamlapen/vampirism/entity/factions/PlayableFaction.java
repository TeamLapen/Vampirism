package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.api.entity.factions.ILordPlayerEntry;
import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<T>> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final ILordPlayerEntry lord;
    private final Supplier<AttachmentType<T>> playerCapabilitySupplier;
    private final Map<IRefinementItem.AccessorySlotType, List<Supplier<IRefinementItem>>> refinementItemBySlot;

    PlayableFaction(@NotNull PlayableFactionBuilder<T> builder) {
        super(builder);
        this.highestLevel = builder.highestLevel;
        this.lord = builder.lord;
        this.playerCapabilitySupplier = builder.playerCapabilitySupplier;
        ImmutableMap.Builder<IRefinementItem.AccessorySlotType, List<Supplier<IRefinementItem>>> refinementItemBuilder = ImmutableMap.builder();
        builder.refinementItemBySlot.forEach((type, list) -> refinementItemBuilder.put(type, List.copyOf(list)));
        this.refinementItemBySlot = refinementItemBuilder.build();
    }

    @Override
    public int getHighestLordLevel() {
        if (lord == null) return 0;
        return this.lord.maxLevel();
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }

    @Nullable
    @Override
    public ILordTitleProvider lordTiles() {
        if (lord == null) return null;
        return this.lord.lordTitleFunction();
    }

    @Override
    public @NotNull T getPlayerCapability(@NotNull Player player) {
        return player.getData(playerCapabilitySupplier.get());
    }

    @Override
    public boolean hasRefinements() {
        return !this.refinementItemBySlot.isEmpty();
    }

    @Override
    public <Z extends Item & IRefinementItem> Z getRandomRefinementItem(RandomSource random, IRefinementItem.AccessorySlotType type) {
        List<Supplier<IRefinementItem>> iRefinementItems = this.refinementItemBySlot.get(type);
        assert iRefinementItems != null && !iRefinementItems.isEmpty();
        return ((Z) iRefinementItems.get(random.nextInt(iRefinementItems.size())).get());
    }

    @Override
    public Collection<IRefinementItem> getRefinementItems() {
        return this.refinementItemBySlot.values().stream().flatMap(Collection::stream).map(Supplier::get).distinct().collect(Collectors.toList());
    }

    @Override
    public Collection<IRefinementItem> getRefinementItems(IRefinementItem.AccessorySlotType type) {
        return this.refinementItemBySlot.getOrDefault(type, List.of()).stream().map(Supplier::get).collect(Collectors.toList());
    }
}
