package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ItemDataUtils {

    public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static ItemStack createPotion(Holder<Potion> potion) {
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    public static ItemStack setEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.get(DataComponents.ENCHANTMENTS));
        mutable.set(enchantment, level);
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    public static ItemStack createEnchantment(Item item, Enchantment enchantment, int level) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(enchantment, level);
        ItemStack itemStack = item.getDefaultInstance();
        itemStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return itemStack;
    }

    public static ItemStack setOil(ItemStack stack, Holder<IOil> oil) {
        stack.set(ModDataComponents.OIL, new OilContent(oil));
        return stack;
    }

    public static ItemStack createOil(Item item, Holder<IOil> oil) {
        ItemStack itemStack = item.getDefaultInstance();
        return setOil(itemStack, oil);
    }
}
