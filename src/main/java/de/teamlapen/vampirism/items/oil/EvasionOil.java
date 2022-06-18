package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class EvasionOil extends ApplicableOil implements IArmorOil {

    public EvasionOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ModTags.Items.APPLICABLE_OIL_ARMOR.contains(stack.getItem()) == VampirismConfig.BALANCE.itApplicableOilArmorReverse.get();
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
        tooltips.add(new TranslationTextComponent("oil.vampirism.evasion.desc").withStyle(TextFormatting.GRAY));
    }

    /**
     * the evasion chance per hit her armor item
     */
    public float evasionChance() {
        return 0.01f;
    }
}
