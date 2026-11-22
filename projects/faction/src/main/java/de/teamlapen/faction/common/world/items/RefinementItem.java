package de.teamlapen.faction.common.world.items;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.AccessorySlotType;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.common.RefinementSet;
import de.teamlapen.faction.common.components.EffectiveRefinementSet;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.library.common.world.items.BaseDisplayItemGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RefinementItem extends Item implements IRefinementItem, BaseDisplayItemGenerator.CreativeTabItemProvider {

    public static final int MAX_DAMAGE = 500;
    private static final RandomSource RANDOM = RandomSource.create();

    private final AccessorySlotType type;

    public RefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.durability(MAX_DAMAGE));
        this.type = type;
    }

    public static ItemStack getRandomRefinementItem(HolderLookup.Provider registries, Holder<? extends IPlayableFaction<?>> faction) {
        var sets = registries.lookupOrThrow(FactionRegistries.Keys.REFINEMENT_SET).listElements().filter(set -> IRefinementSet.isForFaction(set, faction)).map(RefinementSet::weighted).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        Holder<IRefinementSet> s = WeightedList.of(sets).getRandom(RANDOM).orElseGet(() -> sets.getFirst().value());
        List<AccessorySlotType> slotTypes = IRefinementSet.getSlotTypes(s);
        AccessorySlotType t = slotTypes.get(RANDOM.nextInt(slotTypes.size()));
        IRefinementItem i = faction.value().getRandomRefinementItem(RANDOM, t);
        ItemStack stack = new ItemStack(i);
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static @Nullable Holder<IRefinementSet> getRandomRefinementForItem(HolderLookup.Provider registries, @Nullable Holder<? extends IFaction<?>> faction, IRefinementItem stack) {
        var sets = registries.lookupOrThrow(FactionRegistries.Keys.REFINEMENT_SET).listElements().filter(set -> faction == null || IRefinementSet.isForFaction(set, faction)).filter(set -> IRefinementSet.canBeAppliedTo(set, stack.getSlotType())).map(RefinementSet::weighted).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedList.of(sets).getRandom(RANDOM).orElse(null);
    }

    @Override
    public HolderSet<IFaction<?>> getExclusiveFactions(ItemStack stack) {
        return FactionRestriction.get(stack).factions();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, flagIn);
        Holder<IRefinementSet> set = getRefinementSet(stack);
        if (set != null) {
            tooltipComponents.accept(Component.empty());
            tooltipComponents.accept(Component.translatable("tooltip.vampirism.refinement.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));

            Multimap<Holder<Attribute>, AttributeModifier> map = LinkedListMultimap.create();
            for (Holder<IRefinement> refinement : set.value().getRefinements()) {
                IRefinement value = refinement.value();
                Holder<Attribute> attribute = value.getAttribute();
                AttributeModifier modifier = attribute != null ? value.createAttributeModifier(refinement.unwrapKey().orElseThrow().identifier()) : null;
                if (attribute != null && modifier != null) {
                    map.put(attribute, modifier);
                } else {
                    tooltipComponents.accept(value.getDescription().copy().withStyle(ChatFormatting.GRAY));
                }
            }

            AttributeUtil.applyTextFor(stack, tooltipComponents, map, net.neoforged.neoforge.common.util.AttributeTooltipContext.of(FactionsMod.proxy.getClientPlayer(), context, tooltipDisplay, flagIn));
        }
    }

    @Override
    public boolean applyRefinementSet(ItemStack stack, Holder<IRefinementSet> set) {
        if (IRefinementSet.canBeAppliedTo(set, this.type)) {
            stack.set(FactionDataComponents.REFINEMENT_SET, new EffectiveRefinementSet(set));
            return true;
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        Holder<IRefinementSet> set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return Component.translatable(this.getDescriptionId() + ".of", set.value().getName()).withStyle(IRefinementSet.getRarity(set).color);
    }

    @Nullable
    @Override
    public Holder<IRefinementSet> getRefinementSet(ItemStack stack) {
        return stack.getOrDefault(FactionDataComponents.REFINEMENT_SET, EffectiveRefinementSet.EMPTY).set();
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if (IRefinementHandler.get(player).map(sh -> sh.equipRefinement(stack)).orElse(false)) {
                return InteractionResult.CONSUME;
            }

        }
        return super.use(level, player, hand);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = getDefaultInstance();
        parameters.holders().lookupOrThrow(FactionRegistries.Keys.REFINEMENT_SET).listElements().filter(set -> IRefinementSet.isForAnyFaction(set, getExclusiveFactions(stack))).filter(set -> IRefinementSet.canBeAppliedTo(set, getSlotType())).map(set -> {
            ItemStack s = stack.copy();
            applyRefinementSet(s, set);
            return s;
        }).forEach(item -> output.accept(item, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY));
        output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }
}
