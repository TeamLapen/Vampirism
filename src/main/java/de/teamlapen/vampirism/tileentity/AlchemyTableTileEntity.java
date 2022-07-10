package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.blocks.AlchemyTableBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemyTableContainer;
import de.teamlapen.vampirism.inventory.recipes.AbstractBrewingRecipe;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class AlchemyTableTileEntity extends LockableTileEntity implements ITickableTileEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] lastOilCount;
    private Item ingredient;
    private int fuel;
    private int productColor;
    protected final IIntArray dataAccess = new IIntArray() {
        public int get(int slotId) {
            switch(slotId) {
                case 0:
                    return AlchemyTableTileEntity.this.brewTime;
                case 1:
                    return AlchemyTableTileEntity.this.fuel;
                case 2:
                    return AlchemyTableTileEntity.this.productColor;
                default:
                    return 0;
            }
        }

        public void set(int slotId, int value) {
            switch(slotId) {
                case 0:
                    AlchemyTableTileEntity.this.brewTime = value;
                    break;
                case 1:
                    AlchemyTableTileEntity.this.fuel = value;
                    break;
                case 2:
                    AlchemyTableTileEntity.this.productColor = value;
                    break;
            }

        }

        public int getCount() {
            return 3;
        }
    };

    public AlchemyTableTileEntity() {
        super(ModTiles.ALCHEMICAL_TABLE.get());
    }

    @Override
    @Nonnull
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.vampirism.alchemy_table");
    }

    @Nonnull
    @Override
    protected Container createMenu(int menuId, @Nonnull PlayerInventory playerInventory) {
        return new AlchemyTableContainer(menuId, this.level, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean[] getPotionBits() {
        boolean[] aboolean = new boolean[4];

        for(int i = 0; i < 4; ++i) {
            if (!this.items.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int p_70301_1_) {
        return p_70301_1_ >= 0 && p_70301_1_ < this.items.size() ? this.items.get(p_70301_1_) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);

    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ItemStackHelper.takeItem(this.items, p_70304_1_);

    }

    @Override
    public void setItem(int p_70299_1_, @Nonnull ItemStack stack) {
        if (p_70299_1_ >= 0 && p_70299_1_ < this.items.size()) {
            this.items.set(p_70299_1_, stack);
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void tick() {
        ItemStack itemstack = this.items.get(5);
        if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            itemstack.shrink(1);
            this.setChanged();
        }

        boolean flag = this.isBrewable();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = this.items.get(4);
        if (flag1) {
            --this.brewTime;
            boolean flag2 = this.brewTime == 0;
            if (flag2 && flag) {
                this.doBrew();
                this.setChanged();
            } else if (!flag) {
                this.brewTime = 0;
                this.setChanged();
            } else if (this.ingredient != itemstack1.getItem()) {
                this.brewTime = 0;
                this.setChanged();
            }
        } else if (flag && this.fuel > 0) {
            --this.fuel;
            this.brewTime = 600;
            this.ingredient = itemstack1.getItem();
            this.productColor = this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().filter(recipe -> recipe.isInput(this.items.get(4)) && (recipe.isIngredient(items.get(0)) || recipe.isIngredient(items.get(1)))).map(AbstractBrewingRecipe::getResultItem).map(s -> OilUtils.getOil(s).getColor()).findAny().orElse(0xffffff);
            this.setChanged();
        }

        if (!this.level.isClientSide) {
            boolean[] aboolean = this.getPotionBits();
            if (!Arrays.equals(aboolean, this.lastOilCount)) {
                this.lastOilCount = aboolean;
                BlockState blockstate = this.level.getBlockState(this.getBlockPos());
                if (!(blockstate.getBlock() instanceof AlchemyTableBlock)) {
                    return;
                }

                blockstate = blockstate.setValue(AlchemyTableBlock.HAS_BOTTLE_INPUT_0, aboolean[0]);
                blockstate = blockstate.setValue(AlchemyTableBlock.HAS_BOTTLE_INPUT_1, aboolean[1]);
                blockstate = blockstate.setValue(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_0, aboolean[2]);
                blockstate = blockstate.setValue(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_1, aboolean[3]);

                this.level.setBlock(this.worldPosition, blockstate, 2);
            }
        }
    }

    private boolean isIngredient(ItemStack stack) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isIngredient(stack));
    }

    private boolean isInput(ItemStack stack) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isInput(stack));
    }

    private boolean hasRecipe(ItemStack input, ItemStack ingredient) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isInput(input) && recipe.isIngredient(ingredient));
    }

    private boolean isBrewable() {
        ItemStack itemstack = this.items.get(4);
        if (itemstack.isEmpty()) {
            return false;
        } else if (!isInput(itemstack)) {
            return false;
        } else {
            for(int i = 0; i < 2; ++i) {
                if (!this.items.get(i + 2).isEmpty()) {
                    continue;
                }
                ItemStack itemstack1 = this.items.get(i);
                if (!itemstack1.isEmpty() && hasRecipe(itemstack, itemstack1)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void doBrew() {
        ItemStack itemstack = this.items.get(4);

        for (int i = 0; i < 2; i++) {
            if (this.items.get(i+2).isEmpty()) {
                ItemStack stack = getOutput(itemstack, this.items.get(i));
                this.items.set(i, ItemStack.EMPTY);
                this.items.set(i + 2, stack);
            }
        }
        BlockPos blockpos = this.getBlockPos();
        if (itemstack.hasContainerItem()) {
            ItemStack itemstack1 = itemstack.getContainerItem();
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else if (!this.level.isClientSide) {
                InventoryHelper.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
            }
        }
        else itemstack.shrink(1);

        this.items.set(4, itemstack);
        this.level.levelEvent(1035, blockpos, 0);
    }

        public boolean canPlaceItem(int p_94041_1_, @Nonnull ItemStack stack) {
        if (p_94041_1_ == 3) {
            return isValidIngredient(stack);
        } else {
            Item item = stack.getItem();
            if (p_94041_1_ == 4) {
                return item == Items.BLAZE_POWDER;
            } else {
                return isValidInput(stack) && this.getItem(p_94041_1_).isEmpty();
            }
        }
    }

    public boolean isValidIngredient(ItemStack stack) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isIngredient(stack));
    }

    public boolean isValidInput(ItemStack stack) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().anyMatch(recipe -> recipe.isInput(stack));
    }

    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().map(recipe -> recipe.getResult(input, ingredient)).filter(a -> !a.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        super.load(state, compound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.items);
        this.brewTime = compound.getShort("BrewTime");
        this.fuel = compound.getByte("Fuel");
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        super.save(compound);
        compound.putShort("BrewTime", (short)this.brewTime);
        ItemStackHelper.saveAllItems(compound, this.items);
        compound.putByte("Fuel", (byte)this.fuel);
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return super.getUpdateTag();
    }
}
