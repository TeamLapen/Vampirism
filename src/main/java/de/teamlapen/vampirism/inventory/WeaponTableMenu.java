package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.inventory.BooleanDataSlot;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModMenus;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Container to handle crafting in the hunter weapon crafting table
 */
public class WeaponTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess worldPos;
    private final @NotNull HunterPlayer hunterPlayer;
    private final @NotNull Player player;
    private final CraftingContainer craftMatrix = new TransientCraftingContainer(this, 4, 4);
    private final ResultContainer craftResult = new ResultContainer();
    private final BooleanDataSlot missingLava = new BooleanDataSlot();
    private final RecipeManager.CachedCheck<CraftingInput, IWeaponTableRecipe> quickCheck;

    public WeaponTableMenu(int id, @NotNull Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(ModMenus.WEAPON_TABLE.get(), id);
        this.worldPos = worldPosCallable;
        this.hunterPlayer = HunterPlayer.get(playerInventory.player);
        this.player = playerInventory.player;
        this.addSlot(new WeaponTableCraftingSlot(playerInventory.player, craftMatrix, craftResult, 0, 144, 46, worldPosCallable));

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 4, 34 + j * 19, 16 + i * 19));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 18 + i1 * 18, 107 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 18 + l * 18, 165));
        }

        this.addDataSlot(missingLava);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get());
        this.slotsChanged(this.craftMatrix);
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, @NotNull Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    public boolean hasLava() {
        return worldPos.evaluate(((world, blockPos) -> world.getBlockState(blockPos).getValue(WeaponTableBlock.LAVA) > 0), false);
    }

    @NotNull
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemStackCopy = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemStackCopy);
            } else if (index >= 17 && index < 44) {
                if (!this.moveItemStackTo(itemstack1, 44, 53, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 44 && index < 53) {
                if (!this.moveItemStackTo(itemstack1, 17, 44, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 17, 53, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStackCopy.getCount() == itemstack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemStackCopy;
    }

    /**
     * @return if there's a recipe available for the given setup, which requires more lava
     */
    public boolean isMissingLava() {
        return missingLava.getB();
    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        this.worldPos.execute((world, pos) -> {
            this.clearContainer(playerIn, craftMatrix);
            for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
                ItemStack itemstack = this.craftMatrix.removeItemNoUpdate(i);

                if (!itemstack.isEmpty()) {
                    playerIn.drop(itemstack, false);
                }
            }
            missingLava.set(false);
        });
    }

    @Override
    public void slotsChanged(@NotNull Container inventoryIn) {
        this.worldPos.execute((world, pos) -> slotChangedCraftingGrid(world, this.player, this.hunterPlayer, CraftingInput.of(this.craftMatrix.getWidth(), this.craftMatrix.getHeight(), this.craftMatrix.getItems()), this.craftResult));
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(this.worldPos, playerIn, ModBlocks.WEAPON_TABLE.get());
    }

    private void slotChangedCraftingGrid(@NotNull Level worldIn, Player playerIn, @NotNull HunterPlayer hunter, @NotNull CraftingInput craftMatrixIn, @NotNull ResultContainer craftResultIn) {
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer serverPlayer) {
            Optional<RecipeHolder<IWeaponTableRecipe>> optional = quickCheck.getRecipeFor(craftMatrixIn, worldIn);
            this.missingLava.set(false);
            craftResultIn.setItem(0, ItemStack.EMPTY);
            if (optional.isPresent()) {
                RecipeHolder<IWeaponTableRecipe> recipe = optional.get();
                if ((craftResultIn.setRecipeUsed(worldIn, serverPlayer, recipe) || ModList.get().isLoaded("fastbench")) && recipe.value().getRequiredLevel() <= hunter.getLevel() && Helper.areSkillsEnabled(hunter.getSkillHandler(), recipe.value().getRequiredSkills())) {
                    this.worldPos.execute((world, pos) -> {
                        if (world.getBlockState(pos).getValue(WeaponTableBlock.LAVA) >= recipe.value().getRequiredLavaUnits()) {
                            craftResultIn.setItem(0, recipe.value().assemble(craftMatrixIn, world.registryAccess()));
                        } else {
                            this.missingLava.set(true);
                        }
                    });
                }
            }
            broadcastChanges();
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, craftResultIn.getItem(0)));
        }
    }

    public static class Factory implements IContainerFactory<WeaponTableMenu> {

        @Override
        public @NotNull WeaponTableMenu create(int windowId, @NotNull Inventory inv, @NotNull RegistryFriendlyByteBuf data) {
            BlockPos pos = data.readBlockPos();
            return new WeaponTableMenu(windowId, inv, ContainerLevelAccess.create(inv.player.level(), pos));
        }
    }
}
