package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.items.component.AppliedOilContent;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class ApplicableOil extends Oil implements IApplicableOil {

    protected final int maxDuration;

    public ApplicableOil(int color, int maxDuration) {
        super(color);
        this.maxDuration = maxDuration;
    }

    @Override
    public int getDuration(@NotNull ItemStack stack) {
        return AppliedOilContent.getAppliedOil(stack).filter(s -> s.oil().value() == this).map(AppliedOilContent::duration).orElse(0);
    }

    @Override
    public boolean reduceDuration(@NotNull ItemStack stack, IApplicableOil oil, int amount) {
        return OilUtils.reduceAppliedOilDuration(stack, oil, amount);
    }

    @Override
    public int getMaxDuration(ItemStack stack) {
        return this.maxDuration;
    }

    @Override
    public @NotNull Optional<Component> getToolTipLine(ItemStack stack, @NotNull IApplicableOil oil, int duration, @NotNull TooltipFlag flag) {
        ResourceLocation id = RegUtil.id(oil);
        MutableComponent component = Component.translatable(String.format("oil.%s.%s", id.getNamespace(), id.getPath())).withStyle(ChatFormatting.LIGHT_PURPLE);
        if (oil.hasDuration()) {
            int maxDuration = oil.getMaxDuration(stack);
            float perc = duration / (float) maxDuration;
            ChatFormatting status = perc > 0.5 ? ChatFormatting.GREEN : perc > 0.25 ? ChatFormatting.GOLD : ChatFormatting.RED;
            if (flag.isAdvanced()) {
                component.append(" ").append(Component.literal("%s/%s".formatted( duration, maxDuration)).withStyle(status));
            } else {
                component.append(" ").append(Component.translatable("text.vampirism.oil.wetting_status").withStyle(status));
            }
        }
        return Optional.of(component);
    }
}
