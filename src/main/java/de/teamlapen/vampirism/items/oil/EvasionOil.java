package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EvasionOil extends ApplicableOil implements IArmorOil {

    public EvasionOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(@NotNull ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && stack.is(ModItemTags.APPLICABLE_OIL_ARMOR) == VampirismConfig.BALANCE.itApplicableOilArmorReverse.get();
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
        tooltips.add(Component.empty());
        tooltips.add(Component.translatable("text.vampirism.oil.evasion_armor_desc").withStyle(ChatFormatting.DARK_PURPLE));
        tooltips.add(Component.literal("- ").append(Component.translatable("text.vampirism.oil.evasion_chance_desc")).withStyle(ChatFormatting.GRAY));
    }

    /**
     * the evasion chance per hit her armor item
     */
    public float evasionChance() {
        return 0.01f;
    }
}
