package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.items.component.EffectiveRefinementSet;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class RefinementItem extends Item implements IRefinementItem, ModDisplayItemGenerator.CreativeTabItemProvider {

    public static final int MAX_DAMAGE = 500;
    private static final RandomSource RANDOM = RandomSource.create();

    public static @NotNull ItemStack getRandomRefinementItem(@NotNull IPlayableFaction<?> faction) {
        List< WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::data).orElseGet(() -> sets.getFirst().data());
        AccessorySlotType t = s.getSlotType().orElseGet(() -> switch (RANDOM.nextInt(3)) {
            case 0 -> AccessorySlotType.OBI_BELT;
            case 1 -> AccessorySlotType.RING;
            default -> AccessorySlotType.AMULET;
        });
        IRefinementItem i = faction.getRefinementItem(t);
        ItemStack stack = new ItemStack(i);
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static @Nullable IRefinementSet getRandomRefinementForItem(@Nullable IFaction<?> faction, @NotNull IRefinementItem stack) {
        List<WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> faction == null || set.getFaction() == faction).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::data).orElse(null);
    }

    private final AccessorySlotType type;

    public RefinementItem(@NotNull Properties properties, AccessorySlotType type) {
        super(properties.durability(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            set.getRefinements().stream().map(Supplier::get).forEach(refinement -> tooltip.add(Component.literal(" - ").append(refinement.getDescription()).withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public boolean applyRefinementSet(@NotNull ItemStack stack, @NotNull IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            stack.set(ModDataComponents.REFINEMENT_SET, new EffectiveRefinementSet(set));
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return Component.translatable(this.getDescriptionId()).append(" ").append(set.getName()).withStyle(set.getRarity().color);
    }

    @Nullable
    @Override
    public IRefinementSet getRefinementSet(@NotNull ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.REFINEMENT_SET, EffectiveRefinementSet.EMPTY).set();
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (!worldIn.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (FactionPlayerHandler.getCurrentFactionPlayer(playerIn).map(IFactionPlayer::getSkillHandler).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }

        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ItemStack stack = getDefaultInstance();
        ModRegistries.REFINEMENT_SETS.stream().filter(set -> getExclusiveFaction(stack) == null || set.getFaction() == getExclusiveFaction(stack)).filter(set -> set.getSlotType().map(s -> s == getSlotType()).orElse(true)).map(set -> {
            ItemStack s = stack.copy();
            applyRefinementSet(s, set);
            return s;
        }).forEach(item -> {
            output.accept(item, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        });
        output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }
}
