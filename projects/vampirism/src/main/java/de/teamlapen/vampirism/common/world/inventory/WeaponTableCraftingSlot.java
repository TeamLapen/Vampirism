package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.items.recipes.IWeaponTableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
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

    public WeaponTableCraftingSlot(Player player, CraftingContainer craftingInventory, Container inventoryIn, int index, int xPosition, int yPosition, ContainerLevelAccess worldPosCallable) {
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
    public void onTake(Player playerIn, ItemStack stack) {
        this.checkTakeAchievements(stack);

        final int lava = worldPos.evaluate(((world, blockPos) -> {
            if (world.getBlockState(blockPos).getBlock() instanceof WeaponTableBlock) {
                return world.getBlockState(blockPos).getValue(WeaponTableBlock.LAVA);
            }
            return 0;
        }), 0);

        CraftingInput.Positioned positioned = this.craftMatrix.asPositionedCraftInput();
        CraftingInput craftInput = positioned.input();
        int left = positioned.left();
        int top = positioned.top();

        final IWeaponTableRecipe recipe = findMatchingRecipe(playerIn, HunterPlayer.get(playerIn), lava);

        if (recipe != null && recipe.getRequiredLavaUnits() > 0) {
            worldPos.execute((world, pos) -> {
                int remainingLava = Math.max(0, lava - recipe.getRequiredLavaUnits());
                var state = world.getBlockState(pos);
                if (state.getBlock() instanceof WeaponTableBlock) {
                    world.setBlockAndUpdate(pos, state.setValue(WeaponTableBlock.LAVA, remainingLava));
                }
            });
        }

        CommonHooks.setCraftingPlayer(playerIn);
        NonNullList<ItemStack> remaining = getRemainingItems(craftInput, recipe, playerIn.level());
        CommonHooks.setCraftingPlayer(null);

        for (int row = 0; row < craftInput.height(); row++) {
            for (int col = 0; col < craftInput.width(); col++) {
                int slotIndex = (col + left) + (row + top) * this.craftMatrix.getWidth();
                ItemStack inSlot = this.craftMatrix.getItem(slotIndex);
                ItemStack remainder = remaining.get(col + row * craftInput.width());

                if (!inSlot.isEmpty()) {
                    this.craftMatrix.removeItem(slotIndex, 1);
                    inSlot = this.craftMatrix.getItem(slotIndex);
                }

                if (!remainder.isEmpty()) {
                    if (inSlot.isEmpty()) {
                        this.craftMatrix.setItem(slotIndex, remainder);
                    } else if (ItemStack.isSameItemSameComponents(inSlot, remainder)) {
                        remainder.grow(inSlot.getCount());
                        this.craftMatrix.setItem(slotIndex, remainder);
                    } else if (!this.player.getInventory().add(remainder)) {
                        this.player.drop(remainder, false);
                    }
                }
            }
        }

        worldPos.execute((world, pos) -> {
            if (recipe != null && !world.isClientSide()) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.WEAPON_TABLE_CRAFTING.get(), SoundSource.PLAYERS, 1f, 1f);
            }
        });

        playerIn.awardStat(ModStats.INTERACT_WITH_WEAPON_TABLE.get());
    }

    private NonNullList<ItemStack> getRemainingItems(CraftingInput input, @Nullable IWeaponTableRecipe recipe, Level level) {
        if (recipe != null) {
            return recipe.getRemainingItems(input);
        }
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess()
                    .getRecipeFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(), input, serverLevel)
                    .map(h -> h.value().getRemainingItems(input))
                    .orElseGet(() -> CraftingRecipe.defaultCraftingReminder(input));
        }
        return CraftingRecipe.defaultCraftingReminder(input);
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCraftedBy(this.player, this.amountCrafted);
        }
        this.amountCrafted = 0;
    }

    protected @Nullable IWeaponTableRecipe findMatchingRecipe(Player playerIn, IHunterPlayer factionPlayer, int lava) {
        Optional<RecipeHolder<IWeaponTableRecipe>> optional = VampirismMod.services().recipes().getRecipes()
                .getRecipesFor(ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get(), CraftingInput.of(this.craftMatrix.getWidth(), this.craftMatrix.getHeight(), this.craftMatrix.getItems()), playerIn.level())
                .findFirst();
        if (optional.isPresent()) {
            IWeaponTableRecipe recipe = optional.get().value();
            if (factionPlayer.getLevel() >= recipe.getRequiredLevel()
                    && lava >= recipe.getRequiredLavaUnits()
                    && Helper.areSkillsEnabled(factionPlayer.getSkillHandler(), recipe.getRequiredSkills())) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.checkTakeAchievements(stack);
    }
}
