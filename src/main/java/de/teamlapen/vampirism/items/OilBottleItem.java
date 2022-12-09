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
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OilBottleItem extends Item implements IOilItem, CreativeModeTab.DisplayItemsGenerator {

    public OilBottleItem(@NotNull Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public ItemStack getDefaultInstance() {
        return OilUtils.setOil(super.getDefaultInstance(), ModOils.EMPTY.get());
    }

    @Override
    public @NotNull ItemStack withOil(@NotNull IOil oil) {
        return OilUtils.setOil(getDefaultInstance(), oil);
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        ResourceLocation oil = RegUtil.id(OilUtils.getOil(stack));
        return Component.translatable("oil." + oil.getNamespace() + "." + oil.getPath()).append(" ").append(Component.translatable(this.getDescriptionId(stack)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        OilUtils.getOil(stack).getDescription(stack, tooltips);
    }

    @Override
    public void accept(@NotNull FeatureFlagSet featureFlagSet, CreativeModeTab.@NotNull Output output, boolean hasPermission) {
        for (IOil value : RegUtil.values(ModRegistries.OILS)) {
            if (value == ModOils.EMPTY.get()) continue;
            output.accept(OilUtils.setOil(new ItemStack(this), value));
        }
    }

    @NotNull
    @Override
    public IOil getOil(@NotNull ItemStack stack) {
        return OilUtils.getOil(stack);
    }
}
