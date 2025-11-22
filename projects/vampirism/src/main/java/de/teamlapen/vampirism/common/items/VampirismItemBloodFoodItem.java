package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.items.consume.BloodFoodProperties;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VampirismItemBloodFoodItem extends Item {

    public VampirismItemBloodFoodItem(Properties properties, BloodFoodProperties vampireFood) {
        super(properties.component(ModDataComponents.VAMPIRE_FOOD.get(), vampireFood));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (!Helper.isVampire(livingEntity)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 20));
        }

        return stack;
    }
}
