package de.teamlapen.vampirism.blockentity;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.core.ModDataMaps;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronMenu;
import de.teamlapen.vampirism.mixin.accessor.AbstractFurnaceBlockEntityAccessor;
import de.teamlapen.vampirism.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * slots:  0: liquid, 1: ingredient, 2: result, 3: fuel
 */
public class AlchemicalCauldronBlockEntity extends AbstractFurnaceBlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int[] SLOTS_DOWN = new int[]{0, 1, 2};
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_WEST = new int[]{1};
    private static final int[] SLOTS_FUEL = new int[]{3};

    @Nullable
    private UUID ownerID;
    @Nullable
    private String ownerName;
    private @Nullable AlchemicalCauldronRecipe recipeChecked;

    private final RecipeManager.CachedCheck<Container, AlchemicalCauldronRecipe> quickCheck;


    public AlchemicalCauldronBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.ALCHEMICAL_CAULDRON.get(), pos, state, ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get());
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.quickCheck = RecipeManager.createCheck(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get());
    }


    @Override
    public boolean canOpen(@NotNull Player player) {
        if (super.canOpen(player)) {
            if (!Helper.isHunter(player)) {
                player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
                return false;
            }
            if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.BASIC_ALCHEMY.get())) {
                if (ownerID == null) {
                    setOwnerID(player);
                    return true;
                } else if (ownerID.equals(player.getUUID())) {
                    return true;
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.alchemical_cauldron.other", getOwnerName()), true);
                }
            } else {
                player.displayClientMessage(Component.translatable("text.vampirism.not_learned"), true);
            }
        }
        return false;
    }


    @NotNull
    @Override
    public Component getCustomName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron");
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron.display", ownerName, Component.translatable("tile.vampirism.alchemical_cauldron"));
    }

    public int getLiquidColorClient() {
        ItemStack liquidItem = this.items.getFirst();
        return FluidUtil.getFluidContained(liquidItem).map(fluidStack -> IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack)).orElseGet(() -> {
            var color = liquidItem.getItemHolder().getData(ModDataMaps.LIQUID_COLOR_MAP);
            return color != null ? color : 0x00003B;
        });
    }

    public @NotNull Component getOwnerName() {
        return Component.literal(ownerName == null ? "Unknown" : ownerName);
    }

    @NotNull
    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        } else {
            return side == Direction.UP ? SLOTS_UP : side == Direction.WEST ? SLOTS_WEST : SLOTS_FUEL;
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider holderProvider) {
        CompoundTag compound = super.getUpdateTag(holderProvider);
        if (ownerID != null) compound.putUUID("owner", ownerID);
        if (ownerName != null) compound.putString("owner_name", ownerName);
        ContainerHelper.saveAllItems(compound, this.items, holderProvider);
        return compound;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag compound, HolderLookup.Provider holderProvider) {
        super.handleUpdateTag(compound, holderProvider);
        ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
        ContainerHelper.loadAllItems(compound, this.items, holderProvider);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider holderProvider) {
        super.loadAdditional(compound, holderProvider);
        ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider holderProvider) {
        CompoundTag nbt = pkt.getTag();
        if (hasLevel()) {
            handleUpdateTag(nbt, holderProvider);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider holderProvider) {
        super.saveAdditional(compound, holderProvider);
        if (ownerID != null) {
            compound.putUUID("owner", ownerID);
        }
        if (ownerName != null) {
            compound.putString("owner_name", ownerName);

        }
    }

    @Override
    public void setChanged() {
        if (level != null) {
            super.setChanged();
            BlockState old = level.getBlockState(this.worldPosition);
            BlockState state = old.setValue(AbstractFurnaceBlock.LIT, this.isBurning()).setValue(AlchemicalCauldronBlock.LIQUID, this.items.getFirst().isEmpty() ? 0 : this.isBurning() ? 2 : 1);
            if (old.equals(state)) {
                this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            } else {
                this.level.setBlock(this.worldPosition, state, 3);
            }
        }
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        super.setItem(index, stack);
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack);
        if (flag) {
            this.setChanged();
        }
    }

    public void setOwnerID(@NotNull Player player) {
        ownerID = player.getUUID();
        ownerName = player.getGameProfile().getName();
        this.setChanged();
    }

    /**
     * copy of AbstractFurnaceTileEntity#tick() with modification
     */
    public static void serverTick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull AlchemicalCauldronBlockEntity blockEntity) {
        boolean wasBurning = blockEntity.isBurning();
        boolean dirty = false;
        if (wasBurning) {
            blockEntity.dataAccess.set(0, blockEntity.dataAccess.get(0) - 1); // reduce burntime
        }


        ItemStack itemstackFuel = blockEntity.items.get(3);
        if (blockEntity.isBurning() || !itemstackFuel.isEmpty() && !blockEntity.items.get(0).isEmpty() && !blockEntity.items.get(1).isEmpty()) {
            RecipeHolder<AlchemicalCauldronRecipe> cauldronRecipe = blockEntity.quickCheck.getRecipeFor(blockEntity, level).orElse(null);

            if (cauldronRecipe != null && !blockEntity.isBurning() && ((AbstractFurnaceBlockEntityAccessor) blockEntity).invoke_canBurn(level.registryAccess(), cauldronRecipe, blockEntity.items, blockEntity.getMaxStackSize(), blockEntity) && blockEntity.canPlayerCook(cauldronRecipe.value())) {
                blockEntity.dataAccess.set(0, blockEntity.getBurnDuration(itemstackFuel)); //Set burn time
                blockEntity.dataAccess.set(1, blockEntity.dataAccess.get(0));
                if (blockEntity.isBurning()) {
                    dirty = true;
                    if (itemstackFuel.hasCraftingRemainingItem()) {
                        blockEntity.items.set(3, itemstackFuel.getCraftingRemainingItem());
                    } else if (!itemstackFuel.isEmpty()) {
                        Item item = itemstackFuel.getItem();
                        itemstackFuel.shrink(1);
                        if (itemstackFuel.isEmpty()) {
                            blockEntity.items.set(3, itemstackFuel.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (cauldronRecipe != null && blockEntity.isBurning() && ((AbstractFurnaceBlockEntityAccessor) blockEntity).invoke_canBurn(level.registryAccess(), cauldronRecipe, blockEntity.items, blockEntity.getMaxStackSize(), blockEntity) && blockEntity.canPlayerCook(cauldronRecipe.value())) {
                blockEntity.dataAccess.set(2, blockEntity.dataAccess.get(2) + 1); //Increase cook time
                if (blockEntity.dataAccess.get(2) == blockEntity.dataAccess.get(3)) { //If finished
                    blockEntity.dataAccess.set(2, 0);
                    blockEntity.dataAccess.set(3, AbstractFurnaceBlockEntityAccessor.getTotalCookTime(level, blockEntity));
                    blockEntity.finishCooking(level.registryAccess(), cauldronRecipe);
                    dirty = true;
                }
            } else {
                blockEntity.dataAccess.set(2, 0); //Reset cook time
            }
        } else if (!blockEntity.isBurning() && blockEntity.dataAccess.get(2) > 0) {
            blockEntity.dataAccess.set(2, Mth.clamp(blockEntity.dataAccess.get(2) - 2, 0, blockEntity.dataAccess.get(3)));
        }

        if (wasBurning != blockEntity.isBurning()) {
            dirty = true;
        }


        if (dirty) {
            blockEntity.setChanged();
        }

    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new AlchemicalCauldronMenu(id, player, this, this.dataAccess, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("tile.vampirism.alchemical_cauldron");
    }

    private boolean canPlayerCook(@NotNull AlchemicalCauldronRecipe recipe) {
        if (level == null) return false;
        if (recipeChecked == recipe) return true;
        if (ownerID == null) return false;
        Player playerEntity = this.level.getPlayerByUUID(ownerID);
        if (playerEntity == null || !playerEntity.isAlive()) return false;
        HunterPlayer hunter = HunterPlayer.get(playerEntity);
        boolean canCook = recipe.canBeCooked(hunter.getLevel(), hunter.getSkillHandler());
        if (canCook) {
            recipeChecked = recipe;
            return true;
        } else {
            recipeChecked = null;
            return false;
        }
    }

    /**
     * copy of AbstractFurnaceTileEntity#finishCooking(IRecipe) with modification
     */
    private void finishCooking(RegistryAccess access, @Nullable RecipeHolder<AlchemicalCauldronRecipe> recipe) {
        if (recipe != null && ((AbstractFurnaceBlockEntityAccessor) this).invoke_canBurn(access, recipe, items, getMaxStackSize(), this) && canPlayerCook(recipe.value())) {
            ItemStack itemstackfluid = this.items.get(0);
            ItemStack itemstackingredient = this.items.get(1);
            ItemStack itemstack1result = recipe.value().getResultItem(access);
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (this.level != null && !this.level.isClientSide) {
                this.setRecipeUsed(recipe);
            }

            Either<Ingredient, FluidStack> fluid = recipe.value().getFluid();
            fluid.ifLeft(ingredient -> itemstackfluid.shrink(1));
            fluid.ifRight(fluidStack -> this.items.set(0, FluidUtil.getFluidHandler(itemstackfluid).map(handler -> {
                FluidStack drained = handler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                if (drained.getAmount() < fluidStack.getAmount()) {
                    handler.drain(new FluidStack(fluidStack.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE); //For bucket containers we need to draw at least one bucket size
                }
                return handler.getContainer();
            }).orElse(ItemStack.EMPTY)));
            itemstackingredient.shrink(1);
            recipeChecked = null;
        }
    }

    private boolean isBurning() {
        return this.dataAccess.get(0) > 0;
    }

    private boolean isCooking() {
        return this.dataAccess.get(2) > 0;
    }
}
