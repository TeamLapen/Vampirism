package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IArmorOil;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EvasionOil extends ApplicableOil implements IArmorOil {

    public EvasionOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot().isArmor() && stack.is(ModItemTags.APPLICABLE_OIL_ARMOR) == ModConfig.balance().itApplicableOilArmorReverse.get();
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
        tooltips.accept(Component.translatable("tooltip.vampirism.oil.on_armor").withStyle(ChatFormatting.DARK_PURPLE));
        tooltips.accept(Component.literal("- ").append(Component.translatable("tooltip.vampirism.oil.evasion_chance")).withStyle(ChatFormatting.GRAY));
    }

    /**
     * the evasion chance per hit her armor item
     */
    public float evasionChance() {
        return 0.01f;
    }
}
