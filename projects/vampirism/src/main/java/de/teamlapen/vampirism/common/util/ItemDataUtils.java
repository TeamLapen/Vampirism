package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.world.items.BloodBottleItem;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class ItemDataUtils {

    public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    public static ItemStack createPotion(Holder<Potion> potion) {
        return createPotion(potion, Items.POTION);
    }

    public static ItemStack createPotion(Holder<Potion> potion, Item baseItem) {
        return PotionContents.createItemStack(baseItem, potion);
    }

    public static ItemStack setEnchantment(ItemStack stack, Holder<Enchantment> enchantment, int level) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(stack.get(DataComponents.ENCHANTMENTS));
        mutable.set(enchantment, level);
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    public static ItemStack createEnchantment(Item item, Holder<Enchantment> enchantment, int level) {
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

    public static ItemStack createBloodBottle(int blood) {
        ItemStack itemStack = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        itemStack.set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(blood));
        return itemStack;
    }

    public static ItemStack createFilledBloodBottle() {
        return createBloodBottle(BloodBottleItem.AMOUNT);
    }

    public static ItemStack createBloodContainer(int blood) {
        ItemStack itemStack = new ItemStack(ModBlocks.BLOOD_CONTAINER.get());
        FluidStack fluid = new FluidStack(ModFluids.BLOOD.get(), Math.clamp(blood, 0, BloodContainerBlockEntity.CAPACITY));
        itemStack.set(ModDataComponents.BLOOD_CONTAINER, SimpleFluidContent.copyOf(fluid));
        return itemStack;
    }

    public static ItemStack createFilledBloodContainer() {
        return createBloodContainer(BloodContainerBlockEntity.CAPACITY);
    }
}
