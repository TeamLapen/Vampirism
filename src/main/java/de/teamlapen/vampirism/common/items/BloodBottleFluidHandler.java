package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.components.IBottleBlood;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import static de.teamlapen.vampirism.api.components.IBottleBlood.MULTIPLIER;

/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link IBottleBlood#MULTIPLIER}
 */
public class BloodBottleFluidHandler extends ItemAccessResourceHandler<FluidResource> {

    public BloodBottleFluidHandler(ItemAccess itemAccess) {
        super(itemAccess, 1);
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(ModItems.BLOOD_BOTTLE.get())) {
            return FluidResource.of(ModFluids.BLOOD);
        } else {
            return FluidResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if (accessResource.is(ModItems.BLOOD_BOTTLE.get())) {
            return accessResource.getOrDefault(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY).blood() * MULTIPLIER;
        } else {
            return 0;
        }
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        if (newAmount == 0) {
            if (ModConfig.COMMON.autoConvertGlassBottles.get() && !accessResource.is(Items.GLASS_BOTTLE)) {
                return ItemResource.of(Items.GLASS_BOTTLE);
            } else {
                return ItemResource.of(ModItems.BLOOD_BOTTLE.get()).with(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY);
            }
        } else if (newAmount % MULTIPLIER != 0) {
            return ItemResource.EMPTY;
        } else {
            return ItemResource.of(ModItems.BLOOD_BOTTLE.get()).with(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(newAmount / MULTIPLIER));
        }
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return IBottleBlood.MAX_VALUE * MULTIPLIER;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 1 && resource.getFluid().getFluidType() == ModFluids.BLOOD_TYPE.get();
    }



    /**
     * Returns an amount which is a multiple of capacity%10
     */
    public static int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }

}
