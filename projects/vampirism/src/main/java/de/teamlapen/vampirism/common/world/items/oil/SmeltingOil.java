package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IToolOil;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SmeltingOil extends ApplicableOil implements IToolOil { //TODO create pickaxe/shovel/axe superclass if necessary

    public SmeltingOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean isCorrectTool(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES);
    }

    @Override
    public boolean isOilAllowedOnTool(ItemStack stack) {
        return stack.is(ModItemTags.APPLICABLE_OIL_PICKAXE) == ModConfig.balance().itApplicableOilPickaxeReverse.get();
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
    public void getDescription(ItemStack stack, @Nullable Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltips) {
        tooltips.accept(Component.empty());
        tooltips.accept(Component.translatable("tooltip.vampirism.oil.on_pickaxe").withStyle(ChatFormatting.DARK_PURPLE));
        tooltips.accept(Component.literal("- ").append(Component.translatable("tooltip.vampirism.oil.auto_smelting")).withStyle(ChatFormatting.GRAY));
    }
}
