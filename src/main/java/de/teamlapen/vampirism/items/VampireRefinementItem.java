package de.teamlapen.vampirism.items;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VampireRefinementItem extends Item implements IRefinementItem { //TODO 1.17 rename to RefinementItem and create subclass for Vampires

    public static final int MAX_DAMAGE = 500;
    private static final Random RANDOM = new Random();

    public static ItemStack getRandomRefinementItem(IPlayableFaction<?> faction) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM, sets).getItem();
        AccessorySlotType t = s.getSlotType().orElseGet(() -> {
            switch (RANDOM.nextInt(3)) {
                case 0:
                    return AccessorySlotType.OBI_BELT;
                case 1:
                    return AccessorySlotType.RING;
            }
            return AccessorySlotType.AMULET;
        });
        IRefinementItem i = faction.getRefinementItem(t);
        ItemStack stack = new ItemStack(((Item) i));
        if (i.applyRefinementSet(stack, s)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static IRefinementSet getRandomRefinementForItem(@Nullable IFaction<?> faction, IRefinementItem stack) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> faction == null || set.getFaction() == faction).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedRandom.getRandomItem(RANDOM, sets).getItem();
    }

    public static VampireRefinementItem getItemForType(AccessorySlotType type) { //TODO 1.17 move to vampire subclass
        switch (type) {
            case AMULET:
                return ModItems.AMULET.get();
            case RING:
                return ModItems.RING.get();
            default:
                return ModItems.OBI_BELT.get();
        }
    }
    private final AccessorySlotType type;

    public VampireRefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.defaultDurability(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup itemGroup, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(itemGroup)) {
            ItemStack stack = new ItemStack(this);
            IRefinementSet set = getRandomRefinementForItem(this.getExclusiveFaction(), this);
            if (set != null) {
                this.applyRefinementSet(stack, set);
            }
            items.add(stack);
        }
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() { //TODO 1.17 remove
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            set.getRefinementRegistryObjects().stream().map(RegistryObject::get).forEach(refinement -> {
                tooltip.add(new StringTextComponent(" - ").append(refinement.getDescription()).withStyle(TextFormatting.GRAY));
            });
        }
    }

    @Override
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {
        if (set.getSlotType().map(t -> t == type).orElse(true)) {
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putString("refinement_set", set.getRegistryName().toString());
            return true;
        }
        return false;
    }

    @Override
    public ITextComponent getName(@Nonnull ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getName(stack);
        }
        return new TranslationTextComponent(this.getDescriptionId()).append(" ").append(set.getName()).withStyle(set.getRarity().color);
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
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isClientSide()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (FactionPlayerHandler.getOpt(playerIn).map(v -> v).flatMap(FactionPlayerHandler::getCurrentFactionPlayer).map(ISkillPlayer::getSkillHandler).map(sh -> sh.equipRefinementItem(stack)).orElse(false)) {
                return ActionResult.consume(ItemStack.EMPTY);
            }

        }
        return super.use(worldIn, playerIn, handIn);
    }
}
