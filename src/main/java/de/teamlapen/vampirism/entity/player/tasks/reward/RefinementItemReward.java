package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
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
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RefinementItemReward extends ItemReward {

    public static final MapCodec<RefinementItemReward> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        //noinspection RedundantCast,unchecked
        return inst.group(BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.item.get()).map(IRefinementItem::asItem)),
                IFaction.CODEC.optionalFieldOf("faction").forGetter(i -> (Optional<IFaction<?>>) (Object) Optional.ofNullable(i.faction)),
                StringRepresentable.fromEnum(IRefinementSet.Rarity::values).optionalFieldOf("rarity").forGetter(i -> Optional.ofNullable(i.rarity))
        ).apply(inst, (reward, faction, rarity) -> {
            Preconditions.checkArgument(reward.isEmpty() || reward.get() instanceof IRefinementItem, "Item must be a refinement item");
            Preconditions.checkArgument(reward.isEmpty() || matchFaction(((IRefinementItem) reward.get()).getExclusiveFaction(reward.get().getDefaultInstance()), faction.get()), "Faction must match item faction");
            return new RefinementItemReward((IRefinementItem) reward.orElse(null), faction.orElse(null), rarity.orElse(null));
        });
    });

    private static final RandomSource RANDOM = RandomSource.create();

    @NotNull
    private final Supplier<@Nullable IRefinementItem> item;
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
        super(ItemStack.EMPTY);
        this.item = item;
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    private RefinementItemReward(@Nullable IRefinementItem reward, @Nullable IFaction<?> faction, IRefinementSet.@Nullable Rarity rarity) {
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
        if (this.faction != null && !(this.faction instanceof IPlayableFaction<?>)) return ItemStack.EMPTY;
        IPlayableFaction<?> faction = ((IPlayableFaction<?>) this.faction);
        IRefinementItem baseItem = this.item.get();
        if (faction == null) {
            if (baseItem != null) {
                faction = (IPlayableFaction<?>) baseItem.getExclusiveFaction(baseItem.asItem().getDefaultInstance());
            } else {
                faction = getRandomFactionWithAccessories();
            }
            if (faction == null) return ItemStack.EMPTY;
        }
        IPlayableFaction<?> finalFaction = faction;

        Z item = this.item.get() != null ? (Z) this.item.get() : faction.getRefinementItem(IRefinementItem.AccessorySlotType.values()[RANDOM.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
        IRefinementItem.AccessorySlotType slot = (item).getSlotType();
        List< WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream()
                .filter(set -> set.getFaction() == finalFaction)
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
        return Arrays.stream(this.faction != null ? new IPlayableFaction[]{(IPlayableFaction<?>) this.faction} : VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).flatMap(function -> Arrays.stream(IRefinementItem.AccessorySlotType.values()).map(function::getRefinementItem)).map(a -> new ItemStack((Item) a)).collect(Collectors.toList());
    }

    @Nullable
    private static IPlayableFaction<?> getRandomFactionWithAccessories() {
        List<IPlayableFaction<?>> factions = Arrays.stream(VampirismAPI.factionRegistry().getPlayableFactions()).filter(IPlayableFaction::hasRefinements).toList();
        if (factions.isEmpty()) return null;
        return factions.get(RANDOM.nextInt(factions.size()) - 1);
    }

    @Override
    public MapCodec<? extends TaskReward> codec() {
        return CODEC;
    }

    private static boolean matchFaction(IFaction<?> target, IFaction<?> faction) {
        return target == null || faction == null || target == faction;
    }
}
