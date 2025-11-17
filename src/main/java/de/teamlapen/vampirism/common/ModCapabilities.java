package de.teamlapen.vampirism.common;

import de.teamlapen.vampirism.common.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.common.items.BloodSyringeFluidHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public class ModCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Items
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new BloodBottleFluidHandler(access), ModItems.BLOOD_BOTTLE.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new ItemAccessFluidHandler(ItemAccess.forStack(stack), ModDataComponents.BLOOD_CONTAINER.get(), BloodContainerBlockEntity.CAPACITY), ModBlocks.BLOOD_CONTAINER.asItem());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new BloodSyringeFluidHandler(access), ModItems.SYRINGE_EMPTY.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new BloodSyringeFluidHandler(access), ModItems.SYRINGE_BLOOD.get());

        // Blocks
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.BLOOD_CONTAINER.get(), (blockEntity, side) -> blockEntity.fluidInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> {
            if (side == Direction.DOWN) return null;
            return blockEntity.itemHandler;
        });
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> {
            if (side == Direction.UP) return null;
            return blockEntity.fluidInventory;
        });
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.BLOOD_SIEVE.get(), (blockEntity, side) -> {
            if (side != null && side.getAxis().isHorizontal()) return blockEntity.filterItemHandler;
            return null;
        });
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.BLOOD_SIEVE.get(), (blockEntity, side) -> {
            if (side == Direction.UP) return blockEntity.inputFluidInventory;
            if (side == Direction.DOWN) return blockEntity.outputFluidInventory;
            return null;
        });
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntities.ALTAR_INSPIRATION.get(), (blockEntity, side) -> blockEntity.fluidInventory);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.BLOOD_PEDESTAL.get(), (blockEntity, side) -> blockEntity.new ItemWrapper());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.POTION_TABLE.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.ALTAR_INFUSION.get(), (blockEntity, side) -> VanillaContainerWrapper.of(blockEntity));
    }
}
