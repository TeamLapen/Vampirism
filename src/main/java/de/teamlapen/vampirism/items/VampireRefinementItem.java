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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VampireRefinementItem extends Item implements IRefinementItem {

    public static final int MAX_DAMAGE = 500;
    private static final Random RANDOM = new Random();
    private final AccessorySlotType type;

    public VampireRefinementItem(Properties properties, AccessorySlotType type) {
        super(properties.defaultMaxDamage(MAX_DAMAGE).setNoRepair());
        this.type = type;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        IRefinementSet set = getRefinementSet(stack);
        if (set != null) {
            for (IRefinement refinement : set.getRefinements()) {
                tooltip.add(new StringTextComponent(" - ").append(refinement.getDescription()).mergeStyle(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IRefinementSet set = getRefinementSet(stack);
        if (set == null) {
            return super.getDisplayName(stack);
        }
        return new TranslationTextComponent(this.getTranslationKey() + ".of").appendString(" ").append(set.getName()).mergeStyle(set.getRarity().color);
    }

    @Override
    public AccessorySlotType getSlotType() {
        return this.type;
    }

    @Nullable
    @Override
    public IRefinementSet getRefinementSet(ItemStack stack) {
        String refinementsNBT = stack.getOrCreateTag().getString("refinement_set");
        return ModRegistries.REFINEMENT_SETS.getValue(new ResourceLocation(refinementsNBT));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote()){
            ItemStack stack = playerIn.getHeldItem(handIn);
            if(FactionPlayerHandler.getOpt(playerIn).map(v->v).flatMap(FactionPlayerHandler::getCurrentFactionPlayer).map(ISkillPlayer::getSkillHandler).map(sh->sh.equipRefinementItem(stack)).orElse(false)){
                return ActionResult.resultConsume(ItemStack.EMPTY);
            }

        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public static ItemStack getRandomRefinementItem(IFaction<?> faction) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> set.getFaction() == faction).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return ItemStack.EMPTY;
        IRefinementSet s = WeightedRandom.getRandomItem(RANDOM,sets).getItem();
        AccessorySlotType t = s.getSlotType().orElseGet(()->{
            switch (RANDOM.nextInt(3)){
                case 0:
                    return AccessorySlotType.OBI_BELT;
                case 1:
                    return AccessorySlotType.RING;
            }
           return AccessorySlotType.AMULET;
        });
        VampireRefinementItem i  = getItemForType(t);
        ItemStack stack= new ItemStack(i);
        if(i.applyRefinementSet(stack,s)){
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static IRefinementSet getRandomRefinementForItem(@Nullable IFaction<?> faction, VampireRefinementItem stack) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> faction == null || set.getFaction() == faction).filter(set -> set.getSlotType().map(s -> s == stack.getSlotType()).orElse(true)).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return null;
        return WeightedRandom.getRandomItem(RANDOM, sets).getItem();
    }

    public static VampireRefinementItem getItemForType(AccessorySlotType type){
        switch (type){
            case AMULET:
                return ModItems.amulet;
            case RING:
                return ModItems.ring;
            default:
                return ModItems.obi_belt;
        }
    }

    /**
     * Apply refinement set to the given stack.
     * Note: Not all refinements can be applied to all accessory slot types
     * @return Whether the set was successfully applied
     */
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) {
        if(set.getSlotType().map(t -> t==type).orElse(true)){
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putString("refinement_set", set.getRegistryName().toString());
            return true;
        }
        return false;
    }
}
