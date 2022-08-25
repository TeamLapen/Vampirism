package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.items.IArrowContainer;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ArrowContainer extends Item implements IArrowContainer {

    private final Supplier<?extends ArrowItem> item;
    private final int count;

    public ArrowContainer(Properties properties, Supplier<? extends ArrowItem> item, int count) {
        super(properties);
        this.item = item;
        this.count = count;
    }

    @Override
    public Collection<ItemStack> getArrows(ItemStack stack) {
        List<ItemStack> arrows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            arrows.add(new ItemStack(item.get()));
        }
        return arrows;
    }
}
