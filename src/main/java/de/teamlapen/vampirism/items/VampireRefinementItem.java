package de.teamlapen.vampirism.items;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.WeighedRandom;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import de.teamlapen.vampirism.api.items.IRefinementItem.AccessorySlotType;
import net.minecraft.world.item.Item.Properties;

public class VampireRefinementItem extends Item implements IRefinementItem {

    public static final int MAX_DAMAGE = 500;
    private static final Random RANDOM = new Random();

    public static ItemStack getRandomRefinementItem(IFaction<?> faction) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeighedRandom.getRandomItem(RANDOM, sets).getItem();
        AccessorySlotType t = s.getSlotType().orElseGet(() -> {
            switch (RANDOM.nextInt(3)) {
                case 0:
                    return AccessorySlotType.OBI_BELT;
                case 1:
                    return AccessorySlotType.RING;
            }
            return AccessorySlotType.AMULET;
        });
        VampireRefinementItem i = getItemForType(t);
        ItemStack stack = new ItemStack(i);
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static IRefinementSet getRandomRefinementForItem(@Nullable IFaction<?> faction, VampireRefinementItem stack) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> faction == null || set.getFaction() == faction).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeighedRandom.getRandomItem(RANDOM, sets).getItem();
    }

    public static VampireRefinementItem getItemForType(AccessorySlotType type) {
        switch (type) {
            case AMULET:
                return ModItems.amulet;
            case RING:
                return ModItems.ring;
            default:
                return ModItems.obi_belt;
        }
    }
    private final AccessorySlotType type;

    public VampireRefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.defaultDurability(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            for (IRefinement refinement : set.getRefinements()) {
                tooltip.add(new TextComponent(" - ").append(refinement.getDescription()).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    /**
     * Apply refinement set to the given stack.
     * Note: Not all refinements can be applied to all accessory slot types
     *
     * @return Whether the set was successfully applied
     */
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("refinement_set", set.getRegistryName().toString());
            return true;
        }
        return false;
    }

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return new TranslatableComponent(this.getDescriptionId()).append(" ").append(set.getName()).withStyle(set.getRarity().color);
    }

    @Nullable
    @Override
    public IRefinementSet getRefinementSet(ItemStack stack) {
        String refinementsNBT = stack.getOrCreateTag().getString("refinement_set");
        return ModRegistries.REFINEMENT_SETS.getValue(new ResourceLocation(refinementsNBT));
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (FactionPlayerHandler.getOpt(playerIn).map(v -> v).flatMap(FactionPlayerHandler::getCurrentFactionPlayer).map(ISkillPlayer::getSkillHandler).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }

        }
        return super.use(worldIn, playerIn, handIn);
    }
}
