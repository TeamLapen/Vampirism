package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.potion.blood.BloodPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Item blood potion
 */
public class ItemBloodPotion extends VampirismItem {
    private final static String regName = "bloodPotion";

    public ItemBloodPotion() {
        super(regName);
        this.setMaxDamage(1);
        this.setCreativeTab(null);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        BloodPotions.addTooltip(stack, tooltip, HunterPlayer.get(playerIn));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (FactionPlayerHandler.get(playerIn).isInFaction(VReference.HUNTER_FACTION)) {
            playerIn.setActiveHand(hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Nullable
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        if (!worldIn.isRemote) {
            BloodPotions.applyEffects(stack, entityLiving);
        }
        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            if (stack.stackSize <= 0) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }
}
