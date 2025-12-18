package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.api.world.items.IOilItem;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.world.items.component.OilContent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class OilBottleItem extends Item implements IOilItem, BaseDisplayItemGenerator.CreativeTabItemProvider {

    public OilBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        itemStack.set(ModDataComponents.OIL, OilContent.EMPTY);
        return itemStack;
    }

    @Override
    public ItemStack withOil(Holder<IOil> oil) {
        return ItemDataUtils.createOil(this, oil);
    }

    @Override
    public Component getName(ItemStack stack) {
        OilContent oilContents = stack.getOrDefault(ModDataComponents.OIL, OilContent.EMPTY);
        return oilContents.oil().unwrapKey().map(s -> Component.translatable("oil." + s.location().getNamespace() + "." + s.location().getPath()).append(" ")).orElse(Component.empty());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        OilContent.getOil(stack).value().getDescription(stack, context, tooltipDisplay, tooltipComponents);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ModRegistries.OILS.listElements().map(l -> ItemDataUtils.createOil(this, l)).forEach(output::accept);
    }

    @Override
    public Holder<IOil> getOil(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.OIL, OilContent.EMPTY).oil();
    }
}
