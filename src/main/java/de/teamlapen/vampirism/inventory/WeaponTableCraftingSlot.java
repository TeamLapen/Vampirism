package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Result slot for the hunter weapon crafting table
 */
public class WeaponTableCraftingSlot extends Slot {
    private final Player player;
    private final ContainerLevelAccess worldPos;
    private final CraftingContainer craftMatrix;
    private int amountCrafted = 0;

    public WeaponTableCraftingSlot(Player player, CraftingContainer craftingInventory, @NotNull Container inventoryIn, int index, int xPosition, int yPosition, ContainerLevelAccess worldPosCallable) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
        this.worldPos = worldPosCallable;
    }

    @Override
    public boolean mayPlace(@Nullable ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(@NotNull Player playerIn, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);
        final int lava = worldPos.evaluate(((world, blockPos) -> {
            if (world.getBlockState(blockPos).getBlock() instanceof WeaponTableBlock) {
                return world.getBlockState(blockPos).getValue(WeaponTableBlock.LAVA);
            }
            return 0;
        }), 0);
        final IWeaponTableRecipe recipe = findMatchingRecipe(playerIn, HunterPlayer.get(playerIn), lava);
        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            worldPos.execute(((world, pos) -> {
                int remainingLava = Math.max(0, lava - recipe.getRequiredLavaUnits());
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof WeaponTableBlock) {
                    world.setBlockAndUpdate(pos, state.setValue(WeaponTableBlock.LAVA, remainingLava));
                }
            }));
        }

        //Compare with {@link  net.minecraft.world.inventory.ResultSlot#onTake}
        //Remove 1 ingredient from every used input stack, put back potential crafting remainders from the recipe
        CraftingInput.Positioned positionedCraftInput = this.craftMatrix.asPositionedCraftInput();
        CraftingInput craftInput = positionedCraftInput.input();
        net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(playerIn);
        NonNullList<ItemStack> remainders = playerIn.level().getRecipeManager().getRemainingItemsFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(), craftInput, playerIn.level());
        //noinspection DataFlowIssue
        net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(null);


        for (int k = 0; k < craftInput.height(); k++) {
            for (int l = 0; l < craftInput.width(); l++) {
                int slot_index = l + positionedCraftInput.left() + (k + positionedCraftInput.top()) * this.craftMatrix.getWidth();
                ItemStack stackInMatrix = this.craftMatrix.getItem(slot_index);
                ItemStack stackRemainder = remainders.get(l + k * craftInput.width());

                if (!stackInMatrix.isEmpty()) {
                    this.craftMatrix.removeItem(slot_index, 1);
                    stackInMatrix = this.craftMatrix.getItem(slot_index);
                }
                if (!stackRemainder.isEmpty()) {
                    if (stackInMatrix.isEmpty()) {
                        this.craftMatrix.setItem(slot_index, stackRemainder);
                    } else if (ItemStack.isSameItem(stackInMatrix, stackRemainder) && ItemStack.isSameItemSameComponents(stackInMatrix, stackRemainder)) {
                        stackRemainder.grow(stackInMatrix.getCount());
                        this.craftMatrix.setItem(slot_index, stackRemainder);
                    } else if (!this.player.getInventory().add(stackRemainder)) {
                        this.player.drop(stackRemainder, false);
                    }
                }
            }
        }
        worldPos.execute(((world, pos) -> {
            if (recipe != null && !world.isClientSide) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.WEAPON_TABLE_CRAFTING.get(), SoundSource.PLAYERS, 1f, 1f);
            }
        }));
        playerIn.awardStat(ModStats.WEAPON_TABLE.get());
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getItem().getCount());

        }
        return super.remove(amount);
    }

    @Override
    protected void checkTakeAchievements(@NotNull ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCraftedBy(this.player.getCommandSenderWorld(), this.player, this.amountCrafted);
        }

        this.amountCrafted = 0;
    }

    protected @Nullable IWeaponTableRecipe findMatchingRecipe(@NotNull Player playerIn, @NotNull IFactionPlayer<IHunterPlayer> factionPlayer, int lava) {
        Optional<RecipeHolder<IWeaponTableRecipe>> optional = playerIn.getCommandSenderWorld().getRecipeManager().getRecipeFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(), CraftingInput.of(this.craftMatrix.getWidth(), this.craftMatrix.getHeight(), this.craftMatrix.getItems()), playerIn.getCommandSenderWorld());
        if (optional.isPresent()) {
            IWeaponTableRecipe recipe = optional.get().value();
            if (factionPlayer.getLevel() >= recipe.getRequiredLevel() && lava >= recipe.getRequiredLavaUnits() && Helper.areSkillsEnabled(factionPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.checkTakeAchievements(stack);
    }
}
