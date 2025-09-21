package de.teamlapen.vampirism.common.blockentity;

import de.teamlapen.vampirism.common.blocks.BloodInfuserBlock;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.inventory.InfuserMenu;
import de.teamlapen.vampirism.common.inventory.InfuserSlots;
import de.teamlapen.vampirism.common.recipes.InfuserRecipe;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class InfuserBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, InfuserSlots {

    private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INGREDIENT_1, SLOT_INGREDIENT_2, SLOT_INGREDIENT_3, SLOT_INGREDIENT_4, SLOT_INPUT};
    private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT, SLOT_OUTPUT_1, SLOT_OUTPUT_2, SLOT_OUTPUT_3};
    public static final int COOKING_TIMER = 0;
    public static final int TOTAL_COOKING_TIMER = 1;


    protected int cookingTimer;
    protected int totalCookingTime;
    protected NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    protected final RecipeManager.CachedCheck<InfuserRecipe.InfuserRecipeInput, InfuserRecipe> quickCheck;
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case COOKING_TIMER -> InfuserBlockEntity.this.cookingTimer;
                case TOTAL_COOKING_TIMER -> InfuserBlockEntity.this.totalCookingTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case COOKING_TIMER -> InfuserBlockEntity.this.cookingTimer = value;
                case TOTAL_COOKING_TIMER -> InfuserBlockEntity.this.totalCookingTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public InfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.INFUSER.get(), pos, blockState);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.INFUSER_TYPE.get());
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.vampirism.blood_infuser");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new InfuserMenu(containerId, inventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.cookingTimer = tag.getInt("cooking_time_spend");
        this.totalCookingTime = tag.getInt("cooking_total_time");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cooking_time_spend", this.cookingTimer);
        tag.putInt("cooking_total_time", this.totalCookingTime);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, InfuserBlockEntity blockEntity) {
        boolean isActive = blockEntity.cookingTimer > 0;
        boolean changed = false;
        var ingredient1 = blockEntity.getItem(SLOT_INGREDIENT_1);
        var ingredient2 = blockEntity.getItem(SLOT_INGREDIENT_2);
        var ingredient3 = blockEntity.getItem(SLOT_INGREDIENT_3);
        var ingredient4 = blockEntity.getItem(SLOT_INGREDIENT_4);
        var input = blockEntity.getItem(SLOT_INPUT);

        if ((!ingredient1.isEmpty() || !ingredient2.isEmpty() || !ingredient3.isEmpty() || !ingredient4.isEmpty()) && !input.isEmpty()) {
            var recipeInput = new InfuserRecipe.InfuserRecipeInput(ingredient1, ingredient2, ingredient3, ingredient4, input);
            var recipeHolder = blockEntity.quickCheck.getRecipeFor(recipeInput, (ServerLevel) level).orElse(null);

            if (canBurn(level.registryAccess(), recipeHolder, recipeInput, blockEntity.items)) {
                blockEntity.cookingTimer++;
                if (blockEntity.cookingTimer == blockEntity.totalCookingTime) {
                    blockEntity.cookingTimer = 0;
                    blockEntity.totalCookingTime = getTotalCookTime((ServerLevel) level, blockEntity);
                    if (burn(level.registryAccess(), recipeHolder, recipeInput, blockEntity.items)) {

                    }
                    changed = true;
                }
            } else {
                blockEntity.cookingTimer = 0;
            }
        } else if (blockEntity.cookingTimer > 0) {
            blockEntity.cookingTimer = 0;
        }

        if (isActive != blockEntity.cookingTimer > 0) {
            changed = true;
            blockState = blockState.setValue(BloodInfuserBlock.IS_ACTIVE, blockEntity.cookingTimer > 0);
            level.setBlock(blockPos, blockState, 3);
        }

        if (changed) {
            setChanged(level, blockPos, blockState);
        }
    }

    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<InfuserRecipe> recipeHolder, InfuserRecipe.InfuserRecipeInput recipeInput, NonNullList<ItemStack> items) {
        if (recipeHolder == null) {
            return false;
        }
        ItemStack result = recipeHolder.value().assemble(recipeInput, registryAccess);
        if (result.isEmpty()) {
            return false;
        }

        return Stream.of(Pair.of(SLOT_OUTPUT_1, recipeHolder.value().result1()), Pair.of(SLOT_OUTPUT_2, recipeHolder.value().result2()), Pair.of(SLOT_OUTPUT_3, recipeHolder.value().result3()), Pair.of(SLOT_RESULT, result)).noneMatch(pair -> {
            if (!items.get(pair.first()).isEmpty() && !pair.right().isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(items.get(pair.first()), pair.right())) {
                    return true;
                } else {
                    return items.get(pair.first()).getCount() + pair.right().getCount() > items.get(pair.first()).getMaxStackSize();
                }
            }
            return false;
        });
    }

    private static boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<InfuserRecipe> recipeHolder, InfuserRecipe.InfuserRecipeInput recipeInput, NonNullList<ItemStack> items) {
        if (recipeHolder != null && canBurn(registryAccess, recipeHolder, recipeInput, items)) {
            ItemStack result = recipeHolder.value().assemble(recipeInput, registryAccess);

            ItemStack resultSlotItem = items.get(SLOT_RESULT);
            if (resultSlotItem.isEmpty()) {
                items.set(SLOT_RESULT, result);
            } else if (ItemStack.isSameItemSameComponents(resultSlotItem, result) && resultSlotItem.getCount() + result.getCount() <= resultSlotItem.getMaxStackSize()) {
                resultSlotItem.grow(result.getCount());
            }

            ItemStack output1SlotItem = items.get(SLOT_OUTPUT_1);
            ItemStack output1NewItem = recipeHolder.value().result1().copy();
            if (output1SlotItem.isEmpty()) {
                items.set(SLOT_OUTPUT_1, output1NewItem);
            } else if (ItemStack.isSameItemSameComponents(output1SlotItem, output1NewItem) && output1SlotItem.getCount() + output1NewItem.getCount() <= output1SlotItem.getMaxStackSize()) {
                output1SlotItem.grow(output1NewItem.getCount());
            }

            ItemStack output2SlotItem = items.get(SLOT_OUTPUT_2);
            ItemStack output2NewItem = recipeHolder.value().result2().copy();
            if (output2SlotItem.isEmpty()) {
                items.set(SLOT_OUTPUT_2, output2NewItem);
            } else if (ItemStack.isSameItemSameComponents(output2SlotItem, output2NewItem) && output2SlotItem.getCount() + output2NewItem.getCount() <= output2SlotItem.getMaxStackSize()) {
                output2SlotItem.grow(output2NewItem.getCount());
            }

            ItemStack output3SlotItem = items.get(SLOT_OUTPUT_3);
            ItemStack output3NewItem = recipeHolder.value().result3().copy();
            if (output3SlotItem.isEmpty()) {
                items.set(SLOT_OUTPUT_3, output3NewItem);
            } else if (ItemStack.isSameItemSameComponents(output3SlotItem, output3NewItem) && output3SlotItem.getCount() + output3NewItem.getCount() <= output3SlotItem.getMaxStackSize()) {
                output3SlotItem.grow(output3NewItem.getCount());
            }

            items.get(SLOT_INPUT).shrink(1);
            items.get(SLOT_INGREDIENT_1).shrink(1);
            items.get(SLOT_INGREDIENT_2).shrink(1);
            items.get(SLOT_INGREDIENT_3).shrink(1);
            items.get(SLOT_INGREDIENT_4).shrink(1);
            return true;
        }
        return false;
    }

    private static int getTotalCookTime(ServerLevel level, InfuserBlockEntity blockEntity) {
        var input = new InfuserRecipe.InfuserRecipeInput(blockEntity.getItem(SLOT_INGREDIENT_1), blockEntity.getItem(SLOT_INGREDIENT_2), blockEntity.getItem(SLOT_INGREDIENT_3), blockEntity.getItem(SLOT_INGREDIENT_4), blockEntity.getItem(SLOT_INPUT));
        return blockEntity.quickCheck.getRecipeFor(input, level).map(x -> x.value().cookingTime()).orElse(200);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> SLOTS_FOR_UP;
            case DOWN -> SLOTS_FOR_DOWN;
            default -> new int[0];
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return true;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemstack = this.items.get(slot);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if (slot == 0 && !flag && this.level instanceof ServerLevel serverlevel) {
            this.totalCookingTime = getTotalCookTime(serverlevel, this);
            this.cookingTimer = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return switch (slot) {
            case SLOT_INGREDIENT_1, SLOT_INGREDIENT_2, SLOT_INGREDIENT_3, SLOT_INGREDIENT_4, SLOT_INPUT -> true;
            default -> false;
        };
    }

    @Override
    public void fillStackedContents(@NotNull StackedItemContents itemContents) {
        for (ItemStack item : this.items) {
            itemContents.accountStack(item);
        }
    }

}
