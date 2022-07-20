package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class SmeltingOil extends ApplicableOil {

    public SmeltingOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem && ModTags.Items.APPLICABLE_OIL_PICKAXE.contains(stack.getItem()) == VampirismConfig.BALANCE.itApplicableOilPickaxeReverse.get();
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
    public void getDescription(ItemStack stack, List<ITextComponent> tooltips) {
        tooltips.add(new TranslationTextComponent("oil.vampirism.smelt.desc").withStyle(TextFormatting.GRAY));
    }
}
