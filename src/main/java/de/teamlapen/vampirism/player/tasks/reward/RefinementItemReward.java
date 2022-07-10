package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RefinementItemReward extends ItemReward {

    private static final Random RANDOM = new Random();

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

    public RefinementItemReward(@Nullable IFaction<?> faction, @Nonnull Supplier<IRefinementItem> item, @Nullable IRefinementSet.Rarity refinementRarity) {
        super(() -> new ItemStack(item.get()));
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    @Override
    public ItemRewardInstance createInstance(IFactionPlayer<?> player) {
        return new ItemRewardInstance(createItem());
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        return (!this.reward.get().isEmpty() ? Collections.singletonList(new ItemStack(this.reward.get().getItem())) : getAllRefinementItems());
    }

    protected <Z extends Item & IRefinementItem> ItemStack createItem() {
        if (this.faction != null && !(this.faction instanceof IPlayableFaction<?>)) return ItemStack.EMPTY;
        IPlayableFaction<?> faction = ((IPlayableFaction<?>) this.faction);
        if (faction == null) {
            faction = getRandomFactionWithAccessories();
        }
        if (faction == null) return ItemStack.EMPTY;
        IPlayableFaction<?> finalFaction = faction;

        Z item = faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream()
                .filter(set -> set.getFaction() == finalFaction)
                .filter(set -> this.rarity == null || set.getRarity().ordinal() >= this.rarity.ordinal())
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return new ItemStack(item);
        IRefinementSet set = WeightedRandom.getRandomItem(RANDOM, sets).getItem();
        ItemStack stack = new ItemStack(item);
        item.applyRefinementSet(stack, set);
        return stack;
    }

    private List<ItemStack> getAllRefinementItems() {
        return Arrays.stream(this.faction != null ? new IPlayableFaction[]{(IPlayableFaction<?>) this.faction} : VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).flatMap(function -> Arrays.stream(IRefinementItem.AccessorySlotType.values()).map(function::getRefinementItem)).map(a -> new ItemStack((Item) a)).collect(Collectors.toList());
    }

    @Nullable
    private static IPlayableFaction<?> getRandomFactionWithAccessories() {
        List<IPlayableFaction<?>> factions = Arrays.stream(VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).collect(Collectors.toList());
        if (factions.isEmpty()) return null;
        return factions.get(RANDOM.nextInt(factions.size()) - 1);
    }
}