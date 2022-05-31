package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class OilBottle extends Item implements IOilItem {

    public OilBottle(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ItemStack getDefaultInstance() {
        return OilUtils.setOil(super.getDefaultInstance(), ModOils.empty);
    }

    @Override
    public ItemStack withOil(IOil oil) {
        return OilUtils.setOil(getDefaultInstance(), oil);
    }

    @Nonnull
    @Override
    public ITextComponent getName(@Nonnull ItemStack stack) {
        IOil oil = OilUtils.getOil(stack);
        return new TranslationTextComponent("oil." + oil.getRegistryName().getNamespace() + "." + oil.getRegistryName().getPath()).append(" ").append(new TranslationTextComponent(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flag) {
        OilUtils.getOil(stack).getDescription(stack, tooltips);
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup itemGroup, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(itemGroup)) {
            for (IOil value : ModRegistries.OILS.getValues()) {
                if (value == ModOils.empty) continue;
                items.add(OilUtils.setOil(new ItemStack(this), value));
            }
        }
    }

    @Nonnull
    @Override
    public IOil getOil(ItemStack stack) {
        return OilUtils.getOil(stack);
    }
}
