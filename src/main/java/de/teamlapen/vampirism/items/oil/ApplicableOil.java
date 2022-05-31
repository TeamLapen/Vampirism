package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public abstract class ApplicableOil extends Oil implements IApplicableOil {

    protected final int maxDuration;

    public ApplicableOil(int color, int maxDuration) {
        super(color);
        this.maxDuration = maxDuration;
    }

    @Override
    public int getDuration(ItemStack stack) {
        return OilUtils.getAppliedOilStatus(stack).filter(p -> p.getLeft() == this).map(Pair::getRight).orElse(0);
    }

    @Override
    public boolean reduceDuration(ItemStack stack, IApplicableOil oil, int amount) {
        return OilUtils.reduceAppliedOilDuration(stack, oil, amount);
    }

    @Override
    public int getMaxDuration(ItemStack stack) {
        return this.maxDuration;
    }

    @Override
    public Optional<ITextComponent> getToolTipLine(ItemStack stack, IApplicableOil oil, int duration, ITooltipFlag flag) {
        IFormattableTextComponent component = new TranslationTextComponent(String.format("oil.%s.%s",this.getRegistryName().getNamespace(), this.getRegistryName().getPath())).withStyle(TextFormatting.LIGHT_PURPLE);
        if (oil.hasDuration()) {
            int maxDuration = oil.getMaxDuration(stack);
            float perc = duration / (float) maxDuration;
            TextFormatting status = perc > 0.5 ? TextFormatting.GREEN : perc > 0.25 ? TextFormatting.GOLD : TextFormatting.RED;
            if (flag.isAdvanced()) {
                component.append(" ").append(new TranslationTextComponent("%s/%s", duration, maxDuration).withStyle(status));
            } else {
                component.append(" ").append(new TranslationTextComponent("Status").withStyle(status));
            }
        }
        return Optional.of(component);
    }
}
