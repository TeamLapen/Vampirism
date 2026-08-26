package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.common.util.OilUtils;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class ApplicableOil extends Oil implements IApplicableOil {

    protected final int maxDuration;

    public ApplicableOil(int color, int maxDuration) {
        super(color);
        this.maxDuration = maxDuration;
    }

    @Override
    public int getDuration(ItemStack stack) {
        return AppliedOilContent.getAppliedOil(stack).filter(s -> s.oil().value() == this).map(AppliedOilContent::duration).orElse(0);
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
    public void getDescription(ItemStack stack, Item.@Nullable TooltipContext context, TooltipDisplay display, Consumer<Component> tooltips) {
        super.getDescription(stack, context, display, tooltips);
        tooltips.accept(Component.translatable("tooltip.vampirism.oil.lasts", getMaxDuration(stack)).withStyle(ChatFormatting.GRAY));
        tooltips.accept(Component.empty());
        tooltips.accept(getDescriptionTitle().copy().withStyle(ChatFormatting.DARK_PURPLE));
        getEffectDescription(context).forEach(tooltips);
    }
}
