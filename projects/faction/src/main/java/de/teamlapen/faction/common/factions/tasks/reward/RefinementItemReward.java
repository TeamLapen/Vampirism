package de.teamlapen.faction.common.factions.tasks.reward;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.tasks.ITaskRewardInstance;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.util.ModCodecs;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RefinementItemReward implements IItemReward {

    public static final MapCodec<RefinementItemReward> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ModCodecs.playableFactionSet().fieldOf("faction").forGetter(i -> i.faction),
            ItemStackTemplate.CODEC.optionalFieldOf("item").forGetter(i -> Optional.ofNullable(i.item)),
            StringRepresentable.fromEnum(IRefinementSet.Rarity::values).optionalFieldOf("rarity").forGetter(i -> Optional.ofNullable(i.rarity))
    ).apply(inst, (a,b,c) -> new RefinementItemReward(a,b.orElse(null),c.orElse(null))));

    private final @Nullable ItemStackTemplate item;
    private final HolderSet<? extends IPlayableFaction<?>> faction;
    @Nullable
    private final IRefinementSet.Rarity rarity;

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction) {
        this(faction, null);
    }

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction, @Nullable IRefinementSet.Rarity refinementRarity) {
        this(faction, null, refinementRarity);
    }

    public RefinementItemReward(HolderSet<? extends IPlayableFaction<?>> faction, @Nullable ItemStackTemplate item, @Nullable IRefinementSet.Rarity refinementRarity) {
        this.item = item;
        this.faction = faction;
        this.rarity = refinementRarity;
    }

    @Override
    public Component description() {
        return this.item != null ? Component.translatable(this.item.item().value().getDescriptionId()) : Component.translatable("task_reward.factionapi.refinement_item");
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return new Instance(createItem(player.asEntity().getRandom()));
    }

    @Override
    public List<ItemStack> getAllPossibleRewards() {
        return this.item != null ? Collections.singletonList(this.item.create()) : getAllRefinementItems();
    }

    protected <Z extends Item & IRefinementItem> ItemStackTemplate createItem(RandomSource random) {
        @SuppressWarnings("unchecked")
        HolderSet<IPlayableFaction<?>> rewardFactions = (HolderSet<IPlayableFaction<?>>) this.faction;

        if (this.item != null && this.item.item() instanceof IRefinementItem iRefinementItem && iRefinementItem.asItem().getDefaultInstance().get(FactionDataComponents.FACTION_RESTRICTION) instanceof FactionRestriction restriction) {
            //noinspection unchecked
            rewardFactions = new AndHolderSet<>(rewardFactions, HolderSet.direct(x -> (Holder<IPlayableFaction<?>>) (Object) x,restriction.factions().stream().toList()));
        }

        @Nullable
        Holder<IPlayableFaction<?>> randomFaction = rewardFactions.getRandomElement(random).orElse(null);

        if (randomFaction == null) return new ItemStackTemplate(Items.AIR);

        @SuppressWarnings("unchecked")
        Z item = this.item != null ? (Z) this.item.item() : randomFaction.value().getRandomRefinementItem(random, IRefinementItem.AccessorySlotType.values()[random.nextInt(IRefinementItem.AccessorySlotType.values().length)]);
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
        return new ItemStackTemplate(item, stack.getComponentsPatch());
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
