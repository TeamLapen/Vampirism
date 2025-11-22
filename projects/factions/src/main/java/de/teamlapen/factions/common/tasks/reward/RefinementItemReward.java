package de.teamlapen.factions.common.tasks.reward;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.refinements.IRefinementSet;
import de.teamlapen.factions.api.tasks.ITaskRewardInstance;
import de.teamlapen.factions.api.tasks.TaskReward;
import de.teamlapen.factions.common.RefinementSet;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.core.FactionDataComponents;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.util.ModCodecs;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RefinementItemReward extends ItemReward {

    public static final MapCodec<RefinementItemReward> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.item.get()).map(IRefinementItem::asItem)),
                ModCodecs.playableFactionSet().fieldOf("faction").forGetter(i -> i.faction),
                StringRepresentable.fromEnum(IRefinementSet.Rarity::values).optionalFieldOf("rarity").forGetter(i -> Optional.ofNullable(i.rarity))
        ).apply(inst, (reward, faction, rarity) -> {
            Preconditions.checkArgument(reward.isEmpty() || reward.get() instanceof IRefinementItem, "Item must be a refinement item");
            return new RefinementItemReward((IRefinementItem) reward.orElse(null), faction, rarity.orElse(null));
        });
    });

    private final Supplier<@Nullable IRefinementItem> item;
    private final HolderSet<? extends IPlayableFaction<?>> faction;
    @Nullable
    private final IRefinementSet.Rarity rarity;

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction) {
        this(faction, null);
    }

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction, @Nullable IRefinementSet.Rarity refinementRarity) {
        this(faction, () -> null, refinementRarity);
    }

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction, Supplier<@Nullable IRefinementItem> item, @Nullable IRefinementSet.Rarity refinementRarity) {
        super(ItemStack.EMPTY);
        this.item = item;
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    private RefinementItemReward(@Nullable IRefinementItem reward, HolderSet<? extends IPlayableFaction<?>> faction, IRefinementSet.@Nullable Rarity rarity) {
        super(ItemStack.EMPTY);
        this.item = () -> reward;
        this.faction = faction;
        this.rarity = rarity;
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return new Instance(createItem(player.asEntity().getRandom()));
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        return !this.reward.isEmpty() ? Collections.singletonList(new ItemStack(this.reward.getItem())) : getAllRefinementItems();
    }

    protected <Z extends Item & IRefinementItem> ItemStack createItem(RandomSource random) {
        @SuppressWarnings("unchecked")
        HolderSet<IPlayableFaction<?>> rewardFactions = (HolderSet<IPlayableFaction<?>>) this.faction;

        if (this.item.get() instanceof IRefinementItem iRefinementItem && iRefinementItem.asItem().getDefaultInstance().get(FactionDataComponents.FACTION_RESTRICTION) instanceof FactionRestriction restriction) {
            //noinspection unchecked
            rewardFactions = new AndHolderSet<>(rewardFactions, HolderSet.direct(x -> (Holder<IPlayableFaction<?>>) (Object) x,restriction.factions().stream().toList()));
        }

        @Nullable
        Holder<IPlayableFaction<?>> randomFaction = rewardFactions.getRandomElement(random).orElse(null);

        if (randomFaction == null) return ItemStack.EMPTY;

        @SuppressWarnings("unchecked")
        Z item = this.item.get() != null ? (Z) this.item.get() : randomFaction.value().getRandomRefinementItem(random, IRefinementItem.AccessorySlotType.values()[random.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List<Weighted<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream()
                .filter(set -> IFaction.is(randomFaction, set.getFaction()))
                .filter(set -> this.rarity == null || set.getRarity().ordinal() >= this.rarity.ordinal())
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        ItemStack stack = new ItemStack(item);
        if (!sets.isEmpty()) {
            WeightedList.of(sets).getRandom(random).ifPresent(x -> item.applyRefinementSet(stack, x));
        }
        return stack;
    }

    private List<ItemStack> getAllRefinementItems() {
        return this.faction.stream().map(Holder::value).filter(IPlayableFaction::hasRefinements).flatMap(faction -> Arrays.stream(IRefinementItem.AccessorySlotType.values()).flatMap(type -> faction.getRefinementItems(type).stream())).map(ItemStack::new).collect(Collectors.toList());
    }

    @Nullable
    private static Holder<? extends IPlayableFaction<?>> getRandomFactionWithAccessories(RandomSource random) {
        //noinspection unchecked,RedundantCast
        List<Holder<? extends IPlayableFaction<?>>> factions = ModRegistries.FACTIONS.listElements().filter(s -> s.value() instanceof IPlayableFaction<?>).map(s -> ((Holder<? extends IPlayableFaction<?>>) (Object) s)).filter(s -> s.value().hasRefinements()).collect(Collectors.toUnmodifiableList());
        if (factions.isEmpty()) return null;
        return factions.get(random.nextInt(factions.size()) - 1);
    }

    @Override
    public MapCodec<? extends TaskReward> codec() {
        return CODEC;
    }
}
