package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.blockentity.BloodContainerBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.NotNull;

public class BloodContainerItem extends BlockItem implements BaseDisplayItemGenerator.CreativeTabItemProvider {

    public BloodContainerItem(Block block, Properties properties) {
        super(block, properties.component(ModDataComponents.BLOOD_CONTAINER, SimpleFluidContent.EMPTY));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        SimpleFluidContent fluidContent = stack.get(ModDataComponents.BLOOD_CONTAINER.get());
        return fluidContent != null && fluidContent.getAmount() > 0 ? 1 : 64;
    }

    @Override
    public void generateCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(this);
        output.accept(BloodBottleItem.createStackWithBlood(BloodContainerBlockEntity.CAPACITY));
    }
}
