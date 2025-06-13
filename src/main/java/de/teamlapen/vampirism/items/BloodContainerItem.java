package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModDataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class BloodContainerItem extends BlockItem {

    public BloodContainerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        SimpleFluidContent fluidContent = stack.get(ModDataComponents.BLOOD_CONTAINER.get());
        return fluidContent != null && fluidContent.getAmount() > 0 ? 1 : 64;
    }
}
