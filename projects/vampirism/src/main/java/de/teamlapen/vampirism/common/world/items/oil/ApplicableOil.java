package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.common.util.OilUtils;
import de.teamlapen.vampirism.common.util.RegUtil;
import de.teamlapen.vampirism.common.world.items.component.AppliedOilContent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

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
    public Optional<Component> getToolTipLine(ItemStack stack, IApplicableOil oil, int duration, TooltipFlag flag) {
        Identifier id = RegUtil.id(oil);
        MutableComponent component = Component.translatable(String.format("oil.%s.%s", id.getNamespace(), id.getPath())).withStyle(ChatFormatting.LIGHT_PURPLE);
        if (oil.hasDuration()) {
            int maxDuration = oil.getMaxDuration(stack);
            float perc = duration / (float) maxDuration;
            ChatFormatting status = perc > 0.5 ? ChatFormatting.GREEN : perc > 0.25 ? ChatFormatting.GOLD : ChatFormatting.RED;
            if (flag.isAdvanced()) {
                component.append(" ").append(Component.literal("%s/%s".formatted( duration, maxDuration)).withStyle(status));
            } else {
                component.append(" ").append(Component.translatable("tooltip.vampirism.oil.wetting").withStyle(status));
            }
        }
        return Optional.of(component);
    }
}
