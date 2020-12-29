package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.BloodGrinderContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BloodGrinderTileEntity extends InventoryTileEntity implements ITickableTileEntity {


    private static List<ItemEntity> getCaptureItems(World worldIn, BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        return worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(posX, posY + 0.5D, posZ, posX + 1D, posY + 1.5D, posZ + 1D), EntityPredicates.IS_ALIVE);
    }

    private int cooldownPull = 0;
    private int cooldownProcess = 0;
    //Used to provide ItemHandler compatibility
    private final IItemHandler itemHandler;
    private final LazyOptional<IItemHandler> itemHandlerOptional;

    public BloodGrinderTileEntity() {
        super(ModTiles.grinder, 1, BloodGrinderContainer.SELECTOR_INFOS);
        this.itemHandler = createWrapper();
        this.itemHandlerOptional = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((side != Direction.DOWN) && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);
        cooldownPull = tagCompound.getInt("cooldown_pull");
        cooldownProcess = tagCompound.getInt("cooldown_process");
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            --this.cooldownPull;
            if (cooldownPull <= 0) {
                cooldownPull = 10;
                this.updatePull();
            }

            --this.cooldownProcess;
            if (cooldownProcess <= 0) {
                cooldownProcess = 10;
                this.updateProcess();
            }

        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("cooldown_pull", cooldownPull);
        compound.putInt("cooldown_process", cooldownProcess);
        return super.write(compound);
    }

    @Nonnull
    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new BloodGrinderContainer(id, player, this, IWorldPosCallable.of(player.player.getEntityWorld(), this.getPos()));
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tile.vampirism.blood_grinder");
    }

    private boolean pullItems() {
        if (world == null) return false;


        boolean flag = de.teamlapen.lib.lib.inventory.InventoryHelper.tryGetItemHandler(this.world, this.pos.up(), Direction.DOWN).map(pair -> {
            IItemHandler handler = pair.getLeft();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extracted = handler.extractItem(i, 1, true);
                if (!extracted.isEmpty()) {
                    ItemStack simulated = ItemHandlerHelper.insertItemStacked(itemHandler, extracted, true);

                    if (simulated.isEmpty()) {
                        extracted = handler.extractItem(i, 1, false);
                        ItemHandlerHelper.insertItemStacked(itemHandler, extracted, false);
                        return true;
                    }

                }
            }
            return false;
        }).orElse(false);

        if (flag) {
            return true;
        } else {
            for (ItemEntity entityItem : getCaptureItems(this.world, this.pos)) {
                ItemStack stack = entityItem.getItem();
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack2 = itemHandler.insertItem(i, stack, true);
                    if (stack2.isEmpty()) {
                        stack2 = itemHandler.insertItem(i, stack, false);
                        if (stack2.getCount() < stack.getCount()) {
                            entityItem.remove();
                        } else {
                            entityItem.setItem(stack2);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

    }

    private void updateProcess() {
        if (world != null && !isEmpty()) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                final int slot = i;
                ItemStack stack = itemHandler.extractItem(i, 1, true);
                int blood = BloodConversionRegistry.getImpureBloodValue(stack.getItem());
                if (blood > 0) {
                    FluidStack fluid = new FluidStack(ModFluids.impure_blood, blood);
                    FluidUtil.getFluidHandler(this.world, this.pos.down(), Direction.UP).ifPresent(handler -> {
                        int filled = handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                        if (filled >= 0.9f * blood) {
                            ItemStack extractedStack = itemHandler.extractItem(slot, 1, false);
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                            this.world.playSound(null, this.getPos(), ModSounds.grinder, SoundCategory.BLOCKS, 0.5f, 0.7f);
                            this.cooldownProcess = MathHelper.clamp(20 * filled / VReference.FOOD_TO_FLUID_BLOOD, 20, 100);
                        }
                    });

                }
            }
        }
    }

    private boolean updatePull() {
        if (!isFull()) {
            boolean flag = pullItems();
            if (flag) {
                this.cooldownPull = 20;
            }
            return flag;
        }
        return false;
    }
}
