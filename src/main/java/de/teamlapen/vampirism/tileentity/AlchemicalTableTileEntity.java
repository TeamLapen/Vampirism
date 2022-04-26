package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemicalTableContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class AlchemicalTableTileEntity extends LockableTileEntity implements ITickableTileEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    private int fuel;
    protected final IIntArray dataAccess = new IIntArray() {
        public int get(int p_221476_1_) {
            switch(p_221476_1_) {
                case 0:
                    return AlchemicalTableTileEntity.this.brewTime;
                case 1:
                    return AlchemicalTableTileEntity.this.fuel;
                default:
                    return 0;
            }
        }

        public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
                case 0:
                    AlchemicalTableTileEntity.this.brewTime = p_221477_2_;
                    break;
                case 1:
                    AlchemicalTableTileEntity.this.fuel = p_221477_2_;
            }

        }

        public int getCount() {
            return 2;
        }
    };

    public AlchemicalTableTileEntity() {
        super(ModTiles.alchemical_table);
    }

    @Override
    @Nonnull
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.vampirism.alchemical_table");
    }

    @Override
    protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
        return new AlchemicalTableContainer(p_213906_1_, IWorldPosCallable.create(this.level, this.worldPosition), p_213906_2_, this,dataAccess);
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
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 2; ++i) {
            if (!this.items.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    @Override
    public ItemStack getItem(int p_70301_1_) {
        return p_70301_1_ >= 0 && p_70301_1_ < this.items.size() ? this.items.get(p_70301_1_) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);

    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ItemStackHelper.takeItem(this.items, p_70304_1_);

    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        if (p_70299_1_ >= 0 && p_70299_1_ < this.items.size()) {
            this.items.set(p_70299_1_, p_70299_2_);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity p_70300_1_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
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
            this.setChanged();
        }

        if (!this.level.isClientSide) {
            boolean[] aboolean = this.getPotionBits();
            if (!Arrays.equals(aboolean, this.lastPotionCount)) {
                this.lastPotionCount = aboolean;
                BlockState blockstate = this.level.getBlockState(this.getBlockPos());
                if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
                    return;
                }

                for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                    blockstate = blockstate.setValue(BrewingStandBlock.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
                }

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
                InventoryHelper.dropItemStack(this.level, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
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
        return this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE).stream().map(recipe -> recipe.getResult(input, ingredient)).findFirst().orElse(ItemStack.EMPTY);
    }

}
