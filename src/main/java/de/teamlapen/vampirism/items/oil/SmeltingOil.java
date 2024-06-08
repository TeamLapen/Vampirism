package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IToolOil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmeltingOil extends ApplicableOil implements IToolOil { //TODO create pickaxe/shovel/axe superclass if necessary

    public SmeltingOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean isCorrectTool(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean isOilAllowedOnTool(ItemStack stack) {
        return stack.is(ModItemTags.APPLICABLE_OIL_PICKAXE) == VampirismConfig.BALANCE.itApplicableOilPickaxeReverse.get();
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
    public void getDescription(ItemStack stack, @Nullable Item.TooltipContext context, @NotNull List<Component> tooltips) {
        tooltips.add(Component.translatable("oil.vampirism.smelt.desc").withStyle(ChatFormatting.GRAY));
    }
}
