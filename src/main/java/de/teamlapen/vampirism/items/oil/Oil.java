package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class Oil extends ForgeRegistryEntry<IOil> implements IOil {

    private final int color;
    private ITextComponent desc;

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
    public boolean canBeAppliedTo(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public String getName(String item) {
        return item + this.getRegistryName().toString().replace(':', '_');
    }

    @Override
    public void getDescription(ItemStack stack, List<ITextComponent> tooltips) {
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
