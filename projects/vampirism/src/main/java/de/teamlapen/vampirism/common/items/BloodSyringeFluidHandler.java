package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class BloodSyringeFluidHandler extends ItemAccessResourceHandler<FluidResource> {

    public static final int LEVELS_PER_FILL = 1;
    public static final int CAPACITY = 50;

    public BloodSyringeFluidHandler(ItemAccess access) {
        super(access, 1);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return resource.is(ModFluids.BLOOD);
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(ModItems.SYRINGE_EMPTY.get())) {
            return FluidResource.EMPTY;
        } else if (accessResource.is(ModItems.SYRINGE_BLOOD.get())) {
            return FluidResource.of(ModFluids.BLOOD.get());
        } else {
            return FluidResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        var resource = getResourceFrom(accessResource, index);
        return resource.isEmpty() ? 0 : CAPACITY;
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        if (newAmount == 0) {
            return ItemResource.of(ModItems.SYRINGE_EMPTY.get());
        } else if (newAmount != CAPACITY) {
            return ItemResource.EMPTY;
        } else {
            return ItemResource.of(ModItems.SYRINGE_BLOOD.get());
        }
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return CAPACITY;
    }

}

