package de.teamlapen.vampirism.items;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.refinements.RefinementSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VampireRefinementItem extends Item implements IRefinementItem {

    private static final Random RANDOM = new Random();
    private final EquipmentSlotType type;

    public VampireRefinementItem(Properties properties, EquipmentSlotType type) {
        super(properties.maxStackSize(1));
        this.type = type;
    }

    @Override
    public EquipmentSlotType getSlotType() {
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

    public static ItemStack applyRandomRefinementSet(ItemStack stack, IFaction<?> faction) {
        List<WeightedRandomItem<IRefinementSet>> sets = ModRegistries.REFINEMENT_SETS.getValues().stream().filter(set -> set.getFaction() == faction).filter(a -> a.getRarity() != Rarity.EPIC).map(a -> ((RefinementSet) a).getWeightedRandom()).collect(Collectors.toList());
        if (sets.isEmpty()) return stack;
        return applyRefinementSet(stack, WeightedRandom.getRandomItem(RANDOM, sets).getItem());
    }

    public static ItemStack applyRefinementSet(ItemStack stack, IRefinementSet set) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString("refinement_set", set.getRegistryName().toString());
        return stack;
    }
}
