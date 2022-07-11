package de.teamlapen.vampirism.items;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RefinementItem extends Item implements IRefinementItem {

    public static final int MAX_DAMAGE = 500;
    private static final RandomSource RANDOM = RandomSource.create();

    public static ItemStack getRandomRefinementItem(IPlayableFaction<?> faction) {
        List<WeightedRandomItem<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedRandomItem::getItem).orElseGet(()->sets.get(0).getItem());
        AccessorySlotType t = s.getSlotType().orElseGet(() -> {
            return switch (RANDOM.nextInt(3)) {
                case 0 -> AccessorySlotType.OBI_BELT;
                case 1 -> AccessorySlotType.RING;
                default -> AccessorySlotType.AMULET;
            };
        });
        IRefinementItem i = faction.getRefinementItem(t);
        ItemStack stack = new ItemStack(((Item) i));
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static IRefinementSet getRandomRefinementForItem(@Nullable IFaction<?> faction, IRefinementItem stack) {
        List<WeightedRandomItem<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> faction == null || set.getFaction() == faction).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedRandomItem::getItem).orElse(null);
    }

    private final AccessorySlotType type;

    public RefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.defaultDurability(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            set.getRefinements().stream().map(RegistryObject::get).forEach(refinement -> {
                tooltip.add(Component.literal(" - ").append(refinement.getDescription()).withStyle(ChatFormatting.GRAY));
            });
        }
    }

    @Override
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("refinement_set", RegUtil.id(set).toString());
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return Component.translatable(this.getDescriptionId()).append(" ").append(set.getName()).withStyle(set.getRarity().color);
    }

    @Nullable
    @Override
    public IRefinementSet getRefinementSet(ItemStack stack) {
        String refinementsNBT = stack.getOrCreateTag().getString("refinement_set");
        return RegUtil.getRefinementSet(new ResourceLocation(refinementsNBT));
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand handIn) {
        if (!worldIn.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (FactionPlayerHandler.getOpt(playerIn).map(v -> v).flatMap(FactionPlayerHandler::getCurrentFactionPlayer).map(IFactionPlayer::getSkillHandler).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }

        }
        return super.use(worldIn, playerIn, handIn);
    }
}
