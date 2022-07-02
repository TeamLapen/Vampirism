package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SmeltingOil extends ApplicableOil {

    public SmeltingOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && stack.is(ModTags.Items.APPLICABLE_OIL_PICKAXE) == VampirismConfig.BALANCE.itApplicableOilPickaxeReverse.get();
    }

    @Override
    public boolean hasDuration() {
        return true;
    }

    @Override
    public int getDurationReduction() {
        return 1;
    }

    @Override
    public void getDescription(ItemStack stack, List<Component> tooltips) {
        tooltips.add(Component.translatable("oil.vampirism.smelt.desc").withStyle(ChatFormatting.GRAY));
    }
}
