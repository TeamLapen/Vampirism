package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.EntityVampireBase;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 19.10.2015.
 */
public class GarlicHelper {

    public static void setMaxGarlic(ItemStack stack) {
        setGarlic(stack, BALANCE.GARLIC_WEAPON_EFFECT_DURABILITY);
    }

    public static void setGarlic(ItemStack stack, int value) {
        if (stack == null || value < 0) {
            return;
        }
        if (stack.getTagCompound() == null) {
            NBTTagCompound nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        stack.getTagCompound().setInteger("vampirism_garlic", value);
    }

    public static IRecipe createGarlicRecipe(final Item weapon) {
        ItemStack output = new ItemStack(weapon);
        setMaxGarlic(output);
        List<ItemStack> input = new ArrayList<ItemStack>();
        ItemStack w = new ItemStack(weapon);
        w.setItemDamage(32767);
        input.add(w);
        input.add(new ItemStack(ModItems.garlic));
        IRecipe recipe = new ShapelessRecipes(output, input) {
            @Override
            public ItemStack getCraftingResult(InventoryCrafting inv) {
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && weapon.equals(stack.getItem())) {
                        ItemStack out = getRecipeOutput().copy();
                        out.setItemDamage(inv.getStackInSlot(i).getItemDamage());
                        return out;
                    }
                }
                return super.getCraftingResult(inv);
            }
        };
        return recipe;
    }

    public static int getGarlicValue(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            return stack.getTagCompound().getInteger("vampirism_garlic");
        }
        return 0;
    }

    public static void onPlayerAttackedEntity(EntityPlayer player, Entity target) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || !(target instanceof EntityLivingBase)) {
            return;
        }
        if (target.canAttackWithItem()) {
            int i = getGarlicValue(stack);
            if (i > 0) {
                if (target instanceof EntityVampireBase || (target instanceof EntityPlayer && VampirePlayer.get((EntityPlayer) target).getLevel() > 0)) {
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.poison.id, 60, 1));
                }
                setGarlic(stack, --i);
            }
        }
    }

}
