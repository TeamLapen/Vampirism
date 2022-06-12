package de.teamlapen.vampirism.tileentity;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * slots:  0: liquid, 1: ingredient, 2: result, 3: fuel
 */
public class AlchemicalCauldronTileEntity extends AbstractFurnaceTileEntity {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int[] SLOTS_DOWN = new int[]{0, 1, 2};
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_WEST = new int[]{1};
    private static final int[] SLOTS_FUEL = new int[]{3};

    @Nullable
    private UUID ownerID;
    @Nullable
    private String ownerName;
    private AlchemicalCauldronRecipe recipeChecked;

    private boolean warnedRecipeType = false;

    public AlchemicalCauldronTileEntity() {
        super(ModTiles.ALCHEMICAL_CAULDRON.get(), ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
    }


    @Override
    public boolean canOpen(PlayerEntity player) {
        if (super.canOpen(player)) {
            if (HunterPlayer.getOpt(player).map(HunterPlayer::getSkillHandler).map(h -> h.isSkillEnabled(HunterSkills.BASIC_ALCHEMY.get())).orElse(false)) {
                if (ownerID == null) {
                    setOwnerID(player);
                    return true;
                } else if (ownerID.equals(player.getUUID())) {
                    return true;
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.alchemical_cauldron.other", getOwnerName()), Util.NIL_UUID);
                }
            } else {
                player.sendMessage(new TranslationTextComponent("text.vampirism.alchemical_cauldron.cannot_use", getOwnerName()), Util.NIL_UUID);
            }
        }
        return false;
    }


    @Nonnull
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron");
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", ownerName, new TranslationTextComponent("tile.vampirism.alchemical_cauldron"));
    }

    @OnlyIn(Dist.CLIENT)
    public int getLiquidColorClient() {
        ItemStack liquidItem = this.items.get(0);
        return FluidUtil.getFluidContained(liquidItem).map(fluidStack -> fluidStack.getFluid().getAttributes().getColor()).orElseGet(() ->ModRecipes.getLiquidColor(liquidItem.getItem()));
    }

    public ITextComponent getOwnerName() {
        return new StringTextComponent(ownerName == null ? "Unknown" : ownerName);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN)
            return SLOTS_DOWN;
        else
            return side == Direction.UP ? SLOTS_UP : side == Direction.WEST ? SLOTS_WEST : SLOTS_FUEL;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = super.getUpdateTag();
        if (ownerID != null) compound.putUUID("owner", ownerID);
        if (ownerName != null) compound.putString("owner_name", ownerName);
        ItemStackHelper.saveAllItems(compound, this.items);
        return compound;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT compound) {
        super.handleUpdateTag(state, compound);
        ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
        ItemStackHelper.loadAllItems(compound, this.items);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
        super.load(state, compound);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        if (hasLevel()) {
            handleUpdateTag(this.level.getBlockState(pkt.getPos()), nbt);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (ownerID != null) {
            compound.putUUID("owner", ownerID);
        }
        if (ownerName != null) {
            compound.putString("owner_name", ownerName);

        }
        return super.save(compound);
    }

    @Override
    public void setChanged() {
        if (level != null) {
            super.setChanged();
            BlockState old = level.getBlockState(this.worldPosition);
            BlockState state = old.setValue(AbstractFurnaceBlock.LIT, this.isBurning()).setValue(AlchemicalCauldronBlock.LIQUID, this.items.get(0).isEmpty() ? 0 : this.isBurning() ? 2 : 1);
            if (old.equals(state)) {
                this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            } else {
                this.level.setBlock(this.worldPosition, state, 3);
            }
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && stack.sameItem(itemstack) && ItemStack.tagMatches(stack, itemstack);
        if (flag) {
            this.setChanged();
        }
    }

    public void setOwnerID(PlayerEntity player) {
        ownerID = player.getUUID();
        ownerName = player.getDisplayName().getString();
        this.setChanged();
    }

    /**
     * copy of AbstractFurnaceTileEntity#tick() with modification
     */
    @Override
    public void tick() {
        if (level == null) return;
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (wasBurning) {
            this.dataAccess.set(0, this.dataAccess.get(0) - 1); // reduce burntime
        }

        if (!this.level.isClientSide) {
            ItemStack itemstackFuel = this.items.get(3);
            if (this.isBurning() || !itemstackFuel.isEmpty() && !this.items.get(0).isEmpty() && !this.items.get(1).isEmpty()) {
                AlchemicalCauldronRecipe cauldronRecipe = null;
                Optional<? extends AbstractCookingRecipe> irecipe = this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level);
                if (irecipe.isPresent() && irecipe.get() instanceof AlchemicalCauldronRecipe) {
                    cauldronRecipe = (AlchemicalCauldronRecipe) irecipe.get();
                } else {
                    if (!warnedRecipeType) {
                        LOGGER.error("Got an unexpected/illegal recipe for recipe type {}. This might break the AlchemicalCauldron and is caused by another mod", this.recipeType);
                        warnedRecipeType = true;
                    }
                }

                if (cauldronRecipe != null && !this.isBurning() && this.canBurn(cauldronRecipe) && this.canPlayerCook(cauldronRecipe)) {
                    dataAccess.set(0, this.getBurnDuration(itemstackFuel)); //Set burn time
                    dataAccess.set(1, dataAccess.get(0));
                    if (this.isBurning()) {
                        dirty = true;
                        if (itemstackFuel.hasContainerItem())
                            this.items.set(3, itemstackFuel.getContainerItem());
                        else if (!itemstackFuel.isEmpty()) {
                            Item item = itemstackFuel.getItem();
                            itemstackFuel.shrink(1);
                            if (itemstackFuel.isEmpty()) {
                                this.items.set(3, itemstackFuel.getContainerItem());
                            }
                        }
                    }
                }

                if (cauldronRecipe != null && this.isBurning() && this.canBurn(cauldronRecipe) && this.canPlayerCook(cauldronRecipe)) {
                    dataAccess.set(2, dataAccess.get(2) + 1); //Increase cook time
                    if (dataAccess.get(2) == dataAccess.get(3)) { //If finished
                        dataAccess.set(2, 0);
                        dataAccess.set(3, this.getTotalCookTime());
                        this.finishCooking(cauldronRecipe);
                        dirty = true;
                    }
                } else {
                    dataAccess.set(2, 0); //Reset cook time
                }
            } else if (!this.isBurning() && dataAccess.get(2) > 0) {
                dataAccess.set(2, MathHelper.clamp(dataAccess.get(2) - 2, 0, dataAccess.get(3)));
            }

            if (wasBurning != this.isBurning()) {
                dirty = true;
            }
        }

        if (dirty) {
            this.setChanged();
        }

    }

    @Nonnull
    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new AlchemicalCauldronContainer(id, player, this, this.dataAccess, level == null ? IWorldPosCallable.NULL : IWorldPosCallable.create(level, worldPosition));
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron");
    }

    private boolean canPlayerCook(@Nonnull AlchemicalCauldronRecipe recipe) {
        if (level == null) return false;
        if (recipeChecked == recipe) return true;
        if (ownerID == null) return false;
        PlayerEntity playerEntity = this.level.getPlayerByUUID(ownerID);
        if (playerEntity == null || !playerEntity.isAlive()) return false;
        boolean canCook = HunterPlayer.getOpt(playerEntity).map(hunter -> recipe.canBeCooked(hunter.getLevel(), hunter.getSkillHandler())).orElse(false);
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
     *
     * @param recipe
     */
    private void finishCooking(AlchemicalCauldronRecipe recipe) {
        if (recipe != null && this.canBurn(recipe) && canPlayerCook(recipe)) {
            ItemStack itemstackfluid = this.items.get(0);
            ItemStack itemstackingredient = this.items.get(1);
            ItemStack itemstack1result = recipe.getResultItem();
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (this.level != null && !this.level.isClientSide) {
                this.setRecipeUsed(recipe);
            }

            Either<Ingredient, FluidStack> fluid = recipe.getFluid();
            fluid.ifLeft(ingredient -> itemstackfluid.shrink(1));
            fluid.ifRight(fluidStack -> {
                this.items.set(0, FluidUtil.getFluidHandler(itemstackfluid).map(handler -> {
                    FluidStack drained = handler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    if (drained.getAmount() < fluidStack.getAmount()) {
                        handler.drain(new FluidStack(fluidStack.getFluid(), FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE); //For bucket containers we need to draw at least one bucket size
                    }
                    return handler.getContainer();
                }).orElse(ItemStack.EMPTY));
            });
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
