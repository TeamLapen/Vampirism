package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.refinements.RefinementSet;
import de.teamlapen.vampirism.misc.VampirismCreativeTab;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class RefinementItem extends Item implements IRefinementItem, VampirismCreativeTab.CreativeTabItemProvider {

    public static final int MAX_DAMAGE = 500;
    private static final RandomSource RANDOM = RandomSource.create();

    public static @NotNull ItemStack getRandomRefinementItem(@NotNull IPlayableFaction<?> faction) {
        List< WeightedEntry.Wrapper<IRefinementSet>> sets = RegUtil.values(ModRegistries.REFINEMENT_SETS).stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::getData).orElseGet(() -> sets.get(0).getData());
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
        return WeightedRandom.getRandomItem(RANDOM, sets).map(WeightedEntry.Wrapper::getData).orElse(null);
    }

    private final AccessorySlotType type;

    public RefinementItem(@NotNull Properties properties, AccessorySlotType type) {
        super(properties.defaultDurability(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            set.getRefinements().stream().map(RegistryObject::get).forEach(refinement -> tooltip.add(Component.literal(" - ").append(refinement.getDescription()).withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public boolean applyRefinementSet(@NotNull ItemStack stack, @NotNull IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putString("refinement_set", RegUtil.id(set).toString());
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
        if (stack.hasTag()) {
            String refinementsNBT = stack.getTag().getString("refinement_set");
            return RegUtil.getRefinementSet(new ResourceLocation(refinementsNBT));
        } else {
            return null;
        }
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (!worldIn.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (FactionPlayerHandler.getOpt(playerIn).map(v -> v).flatMap(FactionPlayerHandler::getCurrentFactionPlayer).map(IFactionPlayer::getSkillHandler).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return InteractionResultHolder.consume(ItemStack.EMPTY);
            }

        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void generateCreativeTab(FeatureFlagSet featureFlagSet, CreativeModeTab.Output output, boolean hasPermission) {
        ItemStack stack = getDefaultInstance();
        StreamSupport.stream(VampirismRegistries.REFINEMENT_SETS.get().spliterator(), false).filter(set ->  getExclusiveFaction(stack) == null || set.getFaction() == getExclusiveFaction(stack)).filter(set -> set.getSlotType().map(s -> s == getSlotType()).orElse(true)).map(set -> {
            ItemStack s = stack.copy();
            applyRefinementSet(s, set);
            return s;
        }).forEach(item -> {
            output.accept(item, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        });
        output.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
    }
}
