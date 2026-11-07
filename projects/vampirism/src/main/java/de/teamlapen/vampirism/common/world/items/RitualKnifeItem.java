package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.items.component.RitualKnifeType;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class RitualKnifeItem extends Item {

    /**
     * @return a template for a ritual knife of the given dimension type
     */
    public static ItemStackTemplate template(RitualKnifeType type) {
        return new ItemStackTemplate(ModItems.RITUAL_KNIFE, DataComponentPatch.builder().set(ModDataComponents.RITUAL_KNIFE_TYPE.get(), type).build());
    }

    public static ItemStack create(RitualKnifeType type) {
        return template(type).create();
    }

    public RitualKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        RitualKnifeType type = itemStack.get(ModDataComponents.RITUAL_KNIFE_TYPE);
        if (type != null) {
            return Component.translatable(type.getTranslationKey());
        }
        return super.getName(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        RitualKnifeType type = itemStack.get(ModDataComponents.RITUAL_KNIFE_TYPE);
        if (type != null) {
            builder.accept(Component.translatable(type.getTooltipKey()).withStyle(type.getColor()));
        }
    }
}
