package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Oil implements IOil {

    private final int color;

    public Oil(int color) {
        this.color = color;
    }

    @Override
    public boolean canEffect(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public float getAdditionalDamage(ItemStack stack, LivingEntity entity, float damage) {
        return 0;
    }

    @Override
    public void getDescription(ItemStack stack, @Nullable Item.TooltipContext context, List<Component> tooltips) {
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
