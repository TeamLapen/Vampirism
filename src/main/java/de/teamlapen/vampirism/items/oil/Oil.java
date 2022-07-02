package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class Oil extends ForgeRegistryEntry<IOil> implements IOil {

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
    public void getDescription(ItemStack stack, List<ITextComponent> tooltips) {
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
