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
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    public static ItemStackTemplate template(Holder<Potion> potion) {
        return new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(potion))
                .build());
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

    public static ItemStackTemplate createBloodBottle(int blood) {
        return new ItemStackTemplate(ModItems.BLOOD_BOTTLE.get(), DataComponentPatch.builder()
                .set(ModDataComponents.BOTTLE_BLOOD.get(), new BottleBlood(blood))
                .build());
    }

    public static ItemStackTemplate createFilledBloodBottle() {
        return createBloodBottle(BloodBottleItem.AMOUNT);
    }

    public static ItemStackTemplate createBloodContainer(int blood) {
        return new ItemStackTemplate(ModBlocks.BLOOD_CONTAINER.asItem(), DataComponentPatch.builder()
//                .set(ModDataComponents.BLOOD_CONTAINER.get(), SimpleFluidContent.copyOf(new FluidStack(ModFluids.BLOOD.get(), Math.clamp(blood, 0, BloodContainerBlockEntity.CAPACITY))))
                .build());
    }

    public static ItemStackTemplate createFilledBloodContainer() {
        return createBloodContainer(BloodContainerBlockEntity.CAPACITY);
    }
}
