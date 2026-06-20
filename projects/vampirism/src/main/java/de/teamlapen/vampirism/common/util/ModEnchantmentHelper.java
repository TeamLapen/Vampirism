package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.common.core.ModEnchantments;
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
        int frugality = mutablefloat.intValue();
        return frugality > 0 && level.getRandom().nextInt(Math.max(3, 5 - frugality)) == 0;
    }
}
