package de.teamlapen.vampirism.entity.player.tasks.reward;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RefinementItemReward extends ItemReward {

    private static final RandomSource RANDOM = RandomSource.create();

    @Nullable
    private final IFaction<?> faction;
    @Nullable
    private final IRefinementSet.Rarity rarity;

    public RefinementItemReward(@Nullable IFaction<?> faction) {
        this(faction, null);
    }

    public RefinementItemReward(@Nullable IFaction<?> faction, @Nullable IRefinementSet.Rarity refinementRarity) {
        this(faction, () -> null, refinementRarity);
    }

    public RefinementItemReward(@Nullable IFaction<?> faction, @NotNull Supplier<@Nullable IRefinementItem> item, @Nullable IRefinementSet.Rarity refinementRarity) {
        super(() -> ItemStack.EMPTY);
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    @Override
    public @NotNull ItemRewardInstance createInstance(IFactionPlayer<?> player) {
        return new ItemRewardInstance(createItem());
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        final ItemStack reward = this.reward.get();
        return !reward.isEmpty() ? Collections.singletonList(new ItemStack(reward.getItem())) : getAllRefinementItems();
    }

    protected <Z extends Item & IRefinementItem> @NotNull ItemStack createItem() {
        if (this.faction != null && !(this.faction instanceof IPlayableFaction<?>)) return ItemStack.EMPTY;
        IPlayableFaction<?> faction = ((IPlayableFaction<?>) this.faction);
        if (faction == null) {
            faction = getRandomFactionWithAccessories();
        }
        if (faction == null) return ItemStack.EMPTY;
        IPlayableFaction<?> finalFaction = faction;

        Z item = faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List< WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream()
                .filter(set -> finalFaction == null || set.getFaction() == finalFaction)
                .filter(set -> this.rarity == null || set.getRarity().ordinal() >= this.rarity.ordinal())
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        ItemStack stack = new ItemStack(item);
        if (!sets.isEmpty()) {
            WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::getData).ifPresent(set -> item.applyRefinementSet(stack, set));
        }
        return stack;
    }

    private @NotNull List<ItemStack> getAllRefinementItems() {
        return Arrays.stream(this.faction != null ? new IPlayableFaction[]{(IPlayableFaction<?>) this.faction} : VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).flatMap(function -> Arrays.stream(IRefinementItem.AccessorySlotType.values()).map(function::getRefinementItem)).map(a -> new ItemStack((Item) a)).collect(Collectors.toList());
    }

    @Nullable
    private static IPlayableFaction<?> getRandomFactionWithAccessories() {
        List<IPlayableFaction<?>> factions = Arrays.stream(VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).toList();
        if (factions.isEmpty()) return null;
        return factions.get(RANDOM.nextInt(factions.size()) - 1);
    }
}
