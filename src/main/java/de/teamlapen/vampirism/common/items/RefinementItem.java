package de.teamlapen.vampirism.common.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.common.items.component.EffectiveRefinementSet;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.common.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class RefinementItem extends Item implements IRefinementItem, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final int MAX_DAMAGE = 500;
    private static final RandomSource RANDOM = RandomSource.create();

    private final AccessorySlotType type;

    public RefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.durability(MAX_DAMAGE));
        this.type = type;
    }

    public static ItemStack getRandomRefinementItem(Holder<? extends IPlayableFaction<?>> faction) {
        List<WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> IFaction.is(faction, set.getFaction())).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::data).orElseGet(() -> sets.getFirst().data());
        AccessorySlotType t = s.getSlotType().orElseGet(() -> switch (RANDOM.nextInt(3)) {
            case 0 -> AccessorySlotType.OBI_BELT;
            case 1 -> AccessorySlotType.RING;
            default -> AccessorySlotType.AMULET;
        });
        IRefinementItem i = faction.value().getRandomRefinementItem(RANDOM, t);
        ItemStack stack = new ItemStack(i);
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static @Nullable IRefinementSet getRandomRefinementForItem(@Nullable Holder<? extends IFaction<?>> faction, IRefinementItem stack) {
        List<WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> faction == null || IFaction.is(faction, set.getFaction())).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::data).orElse(null);
    }

    @Override
    public HolderSet<IFaction<?>> getExclusiveFactions(ItemStack stack) {
        return FactionRestriction.get(stack).factions();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltipComponents, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.translatable("text.vampirism.when_equipped").withStyle(ChatFormatting.DARK_PURPLE));
            for (Holder<IRefinement> holder : set.getRefinements()) {
                IRefinement refinement = holder.value();
                AttributeModifier attributeModifier = refinement.createAttributeModifier(refinement.getModifierValue());
                if (refinement.getAttribute() != null && attributeModifier != null)  {
                    stack.addModifierTooltip(tooltipComponents::add, VampirismMod.proxy.getClientPlayer(), refinement.getAttribute(), attributeModifier);
                } else {
                    tooltipComponents.add(Component.translatable(Util.makeDescriptionId("refinement", ModRegistries.REFINEMENTS.getKey(refinement)) + ".desc").withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            stack.set(ModDataComponents.REFINEMENT_SET, new EffectiveRefinementSet(set));
            return true;
        }
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return Component.translatable(this.getDescriptionId() + ".of", set.getName()).withStyle(set.getRarity().color);
    }

    @Nullable
    @Override
    public IRefinementSet getRefinementSet(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REFINEMENT_SET, EffectiveRefinementSet.EMPTY).set();
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            if (IRefinementHandler.get(player).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return InteractionResult.CONSUME;
            }

        }
        return super.use(level, player, hand);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = getDefaultInstance();
        ModRegistries.REFINEMENT_SETS.stream().filter(set -> IFaction.contains(getExclusiveFactions(stack), set.getFaction())).filter(set -> set.getSlotType().map(s -> s == getSlotType()).orElse(true)).map(set -> {
            ItemStack s = stack.copy();
            applyRefinementSet(s, set);
            return s;
        }).forEach(item -> output.accept(item, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY));
        output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }
}
