package de.teamlapen.vampirism.api.items.oil;


import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IOil {

    /**
     * whether the entity should be effected by the oil
     */
    boolean canEffect(ItemStack stack, LivingEntity entity);

    /**
     * calculates the bonus damage for the entity
     */
    float getAdditionalDamage(ItemStack stack, LivingEntity entity, float damage);

    /**
     * adds oil tooltip lines to the oil item
     */
    void getDescription(ItemStack stack, @Nullable Item.TooltipContext level, List<Component> tooltips);

    /**
     * oil color code
     */
    int getColor();
}
