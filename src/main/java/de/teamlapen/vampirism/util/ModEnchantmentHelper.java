package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

public class ModEnchantmentHelper {

    public static boolean processFrugality(ServerLevel level, ItemStack stack) {
        MutableFloat mutablefloat = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, enchLevel) -> {
            enchantment.value().modifyItemFilteredCount(ModEnchantments.FRUGALITY_DATA.get(), level, enchLevel, stack, mutablefloat);
        });
        return level.getRandom().nextInt(Math.max(2, 4 - mutablefloat.intValue())) == 0;
    }
}
