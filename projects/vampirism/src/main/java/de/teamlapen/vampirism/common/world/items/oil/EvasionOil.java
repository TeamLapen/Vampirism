package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IArmorOil;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EvasionOil extends ApplicableOil implements IArmorOil {

    private final int amplifier;

    public EvasionOil(int color, int maxDuration, int amplifier) {
        super(color, maxDuration);
        this.amplifier = amplifier;
    }

    public EvasionOil(int color, int maxDuration) {
        this(color, maxDuration, 0);
    }

    @Override
    public boolean canBeApplied(ItemInstance stack) {
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot().isArmor() && stack.is(ModItemTags.APPLICABLE_OIL_ARMOR) == ModConfig.balance().itApplicableOilArmorReverse.get();
    }

    @Override
    public boolean hasDuration() {
        return true;
    }

    @Override
    public Component getDescriptionTitle() {
        return Component.translatable("tooltip.vampirism.oil.on_armor");
    }

    @Override
    public List<Component> getEffectDescription(Item.@Nullable TooltipContext context) {
        return List.of(Component.literal(" ").append(Component.translatable("tooltip.vampirism.oil.evasion_chance").withStyle(ChatFormatting.GRAY)));
    }

    /**
     * the evasion chance per hit her armor item
     */
    public float evasionChance() {
        return 0.01f * (amplifier + 1);
    }
}
