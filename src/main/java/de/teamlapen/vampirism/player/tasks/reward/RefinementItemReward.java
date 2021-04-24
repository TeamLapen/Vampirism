package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this(faction, null, refinementRarity);
    }

    public RefinementItemReward(@Nullable IFaction<?> faction, @Nullable VampireRefinementItem item,  @Nullable IRefinementSet.Rarity refinementRarity) {
        super(new ItemStack(item));
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        return (!this.reward.isEmpty() ? Stream.of((VampireRefinementItem) this.reward.getItem()):Arrays.stream(IRefinementItem.AccessorySlotType.values()).map(VampireRefinementItem::getItemForType)).map(ItemStack::new).collect(Collectors.toList());
    }

    @Override
    public ItemRewardInstance createInstance(IFactionPlayer<?> player) {
        return new ItemRewardInstance(createItem());
    }

    protected ItemStack createItem() {
        VampireRefinementItem item = VampireRefinementItem.getItemForType(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream()
                .filter(set -> this.faction == null || set.getFaction() == faction)
                .filter(set-> this.rarity == null|| set.getRarity() == this.rarity)
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return new ItemStack(item);
        IRefinementSet set = WeightedRandom.getRandomItem(RANDOM, sets).getItem();
        ItemStack stack = new ItemStack(item);
        item.applyRefinementSet(stack, set);
        return stack;
    }
}
