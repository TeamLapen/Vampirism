package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.util.FactionCodec;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RefinementItemReward extends ItemReward {

    public static final MapCodec<RefinementItemReward> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.item.get()).map(IRefinementItem::asItem)),
                FactionCodec.playable().optionalFieldOf("faction").forGetter(i -> Optional.ofNullable(i.faction)),
                StringRepresentable.fromEnum(IRefinementSet.Rarity::values).optionalFieldOf("rarity").forGetter(i -> Optional.ofNullable(i.rarity))
        ).apply(inst, (reward, faction, rarity) -> {
            Preconditions.checkArgument(reward.isEmpty() || reward.get() instanceof IRefinementItem, "Item must be a refinement item");
            Preconditions.checkArgument(reward.isEmpty() || IFaction.is(((IRefinementItem) reward.get()).getExclusiveFaction(reward.get().getDefaultInstance()), faction.orElse(null)), "Faction must match item faction");
            return new RefinementItemReward((IRefinementItem) reward.orElse(null), faction.orElse(null), rarity.orElse(null));
        });
    });

    private static final RandomSource RANDOM = RandomSource.create();

    @NotNull
    private final Supplier<@Nullable IRefinementItem> item;
    private final Holder<? extends IPlayableFaction<?>> faction;
    @Nullable
    private final IRefinementSet.Rarity rarity;

    public RefinementItemReward(@Nullable Holder<? extends IPlayableFaction<?>> faction) {
        this(faction, null);
    }

    public RefinementItemReward(@Nullable Holder<? extends IPlayableFaction<?>> faction, @Nullable IRefinementSet.Rarity refinementRarity) {
        this(faction, () -> null, refinementRarity);
    }

    public RefinementItemReward(@Nullable Holder<? extends IPlayableFaction<?>> faction, @NotNull Supplier<@Nullable IRefinementItem> item, @Nullable IRefinementSet.Rarity refinementRarity) {
        super(ItemStack.EMPTY);
        this.item = item;
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    private RefinementItemReward(@Nullable IRefinementItem reward, Holder<? extends IPlayableFaction<?>> faction, IRefinementSet.@Nullable Rarity rarity) {
        super(ItemStack.EMPTY);
        this.item = () -> reward;
        this.faction = faction;
        this.rarity = rarity;
    }

    @Override
    public @NotNull ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return new Instance(createItem());
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        return !this.reward.isEmpty() ? Collections.singletonList(new ItemStack(this.reward.getItem())) : getAllRefinementItems();
    }

    protected <Z extends Item & IRefinementItem> @NotNull ItemStack createItem() {
        Holder<? extends IPlayableFaction<?>> faction = this.faction;
        IRefinementItem baseItem = this.item.get();
        if (faction == null) {
            if (baseItem != null) {
                faction = baseItem.getExclusiveFaction(baseItem.asItem().getDefaultInstance());
            } else {
                faction = getRandomFactionWithAccessories();
            }
            if (faction == null) return ItemStack.EMPTY;
        }
        Holder<? extends IPlayableFaction<?>> finalFaction = faction;

        Z item = this.item.get() != null ? (Z) this.item.get() : faction.value().getRefinementItem(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List< WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream()
                .filter(set -> IFaction.is(set.getFaction(), finalFaction))
                .filter(set -> this.rarity == null || set.getRarity().ordinal() >= this.rarity.ordinal())
                .filter(set -> set.getSlotType().map(slot1 -> slot1 == slot).orElse(true))
                .map(set -> ((RefinementSet) set).getWeightedRandom()).collect(Collectors.toList());
        ItemStack stack = new ItemStack(item);
        if (!sets.isEmpty()) {
            WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::data).ifPresent(set -> item.applyRefinementSet(stack, set));
        }
        return stack;
    }

    private @NotNull List<ItemStack> getAllRefinementItems() {
        Stream<IPlayableFaction<?>> stream = this.faction != null ? Stream.of(this.faction.value()) : ModRegistries.FACTIONS.stream().filter(IPlayableFaction.class::isInstance).map(s -> (IPlayableFaction<?>)s);
        return stream.filter(IPlayableFaction::hasRefinements).flatMap(function -> Arrays.stream(IRefinementItem.AccessorySlotType.values()).map(function::getRefinementItem)).map(a -> new ItemStack((Item) a)).collect(Collectors.toList());
    }

    @Nullable
    private static Holder<? extends IPlayableFaction<?>> getRandomFactionWithAccessories() {
        //noinspection unchecked,RedundantCast
        List<Holder<? extends IPlayableFaction<?>>> factions = ModRegistries.FACTIONS.holders().filter(s -> s.value() instanceof IPlayableFaction<?>).map(s -> ((Holder<? extends IPlayableFaction<?>>) (Object) s)).filter(s -> s.value().hasRefinements()).collect(Collectors.toUnmodifiableList());
        if (factions.isEmpty()) return null;
        return factions.get(RANDOM.nextInt(factions.size()) - 1);
    }

    @Override
    public MapCodec<? extends TaskReward> codec() {
        return CODEC;
    }

    private static boolean matchFaction(Holder<? extends IFaction<?>> target, IFaction<?> faction) {
        return target == null || faction == null || target == faction;
    }
}
