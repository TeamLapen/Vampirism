package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item blood potion
 */
public class ItemBloodPotion extends VampirismItem {
    private final static String regName = "blood_potion";

    public ItemBloodPotion() {
        super(regName, new Properties().defaultMaxDamage(1));
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Minecraft.getInstance().player != null) {
            BloodPotions.addTooltip(stack, tooltip, HunterPlayer.get(Minecraft.getInstance().player));
        }
    }

    @Override
    public EnumAction getUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (FactionPlayerHandler.get(playerIn).isInFaction(VReference.HUNTER_FACTION)) {
            playerIn.setActiveHand(handIn);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

        if (!worldIn.isRemote) {
            BloodPotions.applyEffects(stack, entityLiving);
        }

        if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }
}
