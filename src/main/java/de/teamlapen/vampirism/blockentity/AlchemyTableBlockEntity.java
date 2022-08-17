package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.AlchemyTableBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemyTableContainer;
import de.teamlapen.vampirism.inventory.recipes.AbstractBrewingRecipe;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AlchemyTableBlockEntity extends BaseContainerBlockEntity {

    private @NotNull NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int brewTime;
    private boolean @Nullable [] lastOilCount;
    private Item ingredient;
    private int fuel;
    private int productColor;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int slotId) {
            return switch (slotId) {
                case 0 -> AlchemyTableBlockEntity.this.brewTime;
                case 1 -> AlchemyTableBlockEntity.this.fuel;
                case 2 -> AlchemyTableBlockEntity.this.productColor;
                default -> 0;
            };
        }

        public void set(int slotId, int value) {
            switch (slotId) {
                case 0 -> AlchemyTableBlockEntity.this.brewTime = value;
                case 1 -> AlchemyTableBlockEntity.this.fuel = value;
                case 2 -> AlchemyTableBlockEntity.this.productColor = value;
            }

        }

        public int getCount() {
            return 3;
        }
    };

    public AlchemyTableBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.ALCHEMICAL_TABLE.get(), pos, state);
    }

    @Override
    @NotNull
    protected Component getDefaultName() {
        return Component.translatable("container.vampirism.alchemy_table");
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int menuId, @NotNull Inventory playerInventory) {
        return new AlchemyTableContainer(menuId, this.level, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean[] getPotionBits() {
        boolean[] aboolean = new boolean[4];

        for (int i = 0; i < 4; ++i) {
            if (!this.items.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    @NotNull
    @Override
    public ItemStack getItem(int p_70301_1_) {
        return p_70301_1_ >= 0 && p_70301_1_ < this.items.size() ? this.items.get(p_70301_1_) : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        return ContainerHelper.removeItem(this.items, p_70298_1_, p_70298_2_);

    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ContainerHelper.takeItem(this.items, p_70304_1_);

    }

    @Override
    public void setItem(int p_70299_1_, @NotNull ItemStack stack) {
        if (p_70299_1_ >= 0 && p_70299_1_ < this.items.size()) {
            this.items.set(p_70299_1_, stack);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public static void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull AlchemyTableBlockEntity blockEntity) {
        ItemStack itemstack = blockEntity.items.get(5);
        if (blockEntity.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            blockEntity.fuel = 20;
            itemstack.shrink(1);
            blockEntity.setChanged();
        }

        boolean flag = blockEntity.isBrewable(level);
        boolean flag1 = blockEntity.brewTime > 0;
        ItemStack itemstack1 = blockEntity.items.get(4);
        if (flag1) {
            --blockEntity.brewTime;
            boolean flag2 = blockEntity.brewTime == 0;
            if (flag2 && flag) {
                blockEntity.doBrew(level);
                blockEntity.setChanged();
            } else if (!flag) {
                blockEntity.brewTime = 0;
                blockEntity.setChanged();
            } else if (blockEntity.ingredient != itemstack1.getItem()) {
                blockEntity.brewTime = 0;
                blockEntity.setChanged();
            }
        } else if (flag && blockEntity.fuel > 0) {
            --blockEntity.fuel;
            blockEntity.brewTime = 600;
            blockEntity.ingredient = itemstack1.getItem();
            blockEntity.productColor = level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().filter(recipe -> recipe.isInput(blockEntity.items.get(4)) && (recipe.isIngredient(blockEntity.items.get(0)) || recipe.isIngredient(blockEntity.items.get(1)))).map(AbstractBrewingRecipe::getResultItem).map(s -> OilUtils.getOil(s).getColor()).findAny().orElse(0xffffff);
            blockEntity.setChanged();
        }

        boolean[] aboolean = blockEntity.getPotionBits();
        if (!Arrays.equals(aboolean, blockEntity.lastOilCount)) {
            blockEntity.lastOilCount = aboolean;
            if (!(state.getBlock() instanceof AlchemyTableBlock)) {
                return;
            }

            state = state.setValue(AlchemyTableBlock.HAS_BOTTLE_INPUT_0, aboolean[0]);
            state = state.setValue(AlchemyTableBlock.HAS_BOTTLE_INPUT_1, aboolean[1]);
            state = state.setValue(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_0, aboolean[2]);
            state = state.setValue(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_1, aboolean[3]);

            level.setBlock(pos, state, 2);
        }
    }

    private boolean isBrewable(@NotNull Level level) {
        ItemStack itemstack = this.items.get(4);
        if (itemstack.isEmpty()) {
            return false;
        } else if (!isValidInput(level, itemstack)) {
            return false;
        } else {
            for (int i = 0; i < 2; ++i) {
                if (!this.items.get(i + 2).isEmpty()) {
                    continue;
                }
                ItemStack itemstack1 = this.items.get(i);
                if (!itemstack1.isEmpty() && hasRecipe(level, itemstack, itemstack1)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void doBrew(@NotNull Level level) {
        ItemStack itemstack = this.items.get(4);

        for (int i = 0; i < 2; i++) {
            if (this.items.get(i + 2).isEmpty()) {
                ItemStack stack = getOutput(level, itemstack, this.items.get(i));
                this.items.set(i, ItemStack.EMPTY);
                this.items.set(i + 2, stack);
            }
        }
        BlockPos blockpos = this.getBlockPos();
        if (itemstack.hasCraftingRemainingItem()) {
            ItemStack itemstack1 = itemstack.getCraftingRemainingItem();
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else if (!level.isClientSide) {
                Containers.dropItemStack(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
            }
        } else {
            itemstack.shrink(1);
        }

        this.items.set(4, itemstack);
        level.levelEvent(1035, blockpos, 0);
    }

    public boolean canPlaceItem(int p_94041_1_, @NotNull ItemStack stack) {
        if (p_94041_1_ == 3) {
            return isValidIngredient(this.level, stack);
        } else {
            Item item = stack.getItem();
            if (p_94041_1_ == 4) {
                return item == Items.BLAZE_POWDER;
            } else {
                return isValidInput(this.level, stack) && this.getItem(p_94041_1_).isEmpty();
            }
        }
    }

    private boolean hasRecipe(@NotNull Level level, @NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().anyMatch(recipe -> recipe.isInput(input) && recipe.isIngredient(ingredient));
    }

    public boolean isValidIngredient(@NotNull Level level, @NotNull ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().anyMatch(recipe -> recipe.isIngredient(stack));
    }

    public boolean isValidInput(@NotNull Level level, @NotNull ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().anyMatch(recipe -> recipe.isInput(stack));
    }

    public @NotNull ItemStack getOutput(@NotNull Level level, @NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.ALCHEMICAL_TABLE_TYPE.get()).stream().map(recipe -> recipe.getResult(input, ingredient)).filter(a -> !a.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.brewTime = tag.getShort("BrewTime");
        this.fuel = tag.getByte("Fuel");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("BrewTime", (short) this.brewTime);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putByte("Fuel", (byte) this.fuel);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }
}
