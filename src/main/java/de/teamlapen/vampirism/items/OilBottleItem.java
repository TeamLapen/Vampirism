package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ModDisplayItemGenerator;
import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.items.component.OilContent;
import de.teamlapen.vampirism.util.ItemDataUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OilBottleItem extends Item implements IOilItem, ModDisplayItemGenerator.CreativeTabItemProvider {

    public OilBottleItem(@NotNull Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(this);
        itemStack.set(ModDataComponents.OIL, OilContent.EMPTY);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack withOil(@NotNull Holder<IOil> oil) {
        return ItemDataUtils.createOil(this, oil);
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        OilContent oilContents = stack.getOrDefault(ModDataComponents.OIL, OilContent.EMPTY);
        return oilContents.oil().unwrapKey().map(s -> Component.translatable("oil." + s.location().getNamespace() + "." + s.location().getPath()).append(" ")).orElse(Component.empty()).append(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
        OilContent.getOil(stack).value().getDescription(stack, context, tooltips);
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        ModRegistries.OILS.holders().map(l -> ItemDataUtils.createOil(this, l)).forEach(output::accept);
    }

    @NotNull
    @Override
    public Holder<IOil> getOil(@NotNull ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.OIL, OilContent.EMPTY).oil();
    }
}
