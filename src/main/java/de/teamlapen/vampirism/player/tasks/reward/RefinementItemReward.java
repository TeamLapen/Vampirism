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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RefinementItemReward extends ItemReward {

    private static final Random RANDOM = new Random();

    @Nullable
    private final IFaction<?> faction;
    @Nullable
    private final IRefinementSet.Rarity rarity;

    public RefinementItemReward(@Nullable IFaction<?> faction, @Nullable IRefinementSet.Rarity refinementRarity) {
        this(faction, VampireRefinementItem.getItemForType(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]), refinementRarity);
    }

    public RefinementItemReward(@Nullable IFaction<?> faction, VampireRefinementItem item,  @Nullable IRefinementSet.Rarity refinementRarity) {
        super(new ItemStack(item));
        this.faction = faction;
        this.rarity = refinementRarity;
    }


    @Override
    public void applyReward(IFactionPlayer<?> player) {
        ItemStack stack = createItem();
        if (!player.getRepresentingPlayer().addItemStackToInventory(stack)) {
            player.getRepresentingPlayer().dropItem(stack, true);
        }
    }

    protected ItemStack createItem() {
        IRefinementItem.AccessorySlotType slot = ((VampireRefinementItem) this.reward.getItem()).getSlotType();
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream()
                .filter(set -> this.faction == null || set.getFaction() == faction)
                .filter(set-> this.rarity == null|| set.getRarity() == this.rarity)
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return this.reward;
        IRefinementSet set = WeightedRandom.getRandomItem(RANDOM, sets).getItem();
        ItemStack stack = new ItemStack(this.reward.getItem());
        ((VampireRefinementItem) this.reward.getItem()).applyRefinementSet(stack, set);
        return stack;
    }
}
