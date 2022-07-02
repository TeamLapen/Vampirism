package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.IOilItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
        return OilUtils.setOil(super.getDefaultInstance(), ModOils.EMPTY.get());
    }

    @Override
    public ItemStack withOil(IOil oil) {
        return OilUtils.setOil(getDefaultInstance(), oil);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        ResourceLocation oil = RegUtil.id(OilUtils.getOil(stack));
        return Component.translatable("oil." + oil.getNamespace() + "." + oil.getPath()).append(" ").append(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
        OilUtils.getOil(stack).getDescription(stack, tooltips);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab itemGroup, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowedIn(itemGroup)) {
            for (IOil value : RegUtil.values(ModRegistries.OILS)) {
                if (value == ModOils.EMPTY.get()) continue;
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
