package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.crafting.AlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;


public class AlchemicalCauldronTileEntity extends AbstractFurnaceTileEntity {

    private final static Logger LOGGER = LogManager.getLogger(AlchemicalCauldronTileEntity.class);
    private static final int SLOT_RESULT = 0;
    private static final int SLOT_LIQUID = 1;
    private static final int SLOT_INGREDIENT = 2;
    private static final int SLOT_FUEL = 3;
    private static final int[] SLOTS_TOP = new int[]{SLOT_RESULT};
    private static final int[] SLOTS_WEST = new int[]{SLOT_LIQUID};
    private static final int[] SLOTS_EAST = new int[]{SLOT_INGREDIENT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_FUEL};

    public AlchemicalCauldronTileEntity() {
        super(ModTiles.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.alchemical_cauldron");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new AlchemicalCauldronContainer(id, player, this, field_214013_b);//TODO 1.14 field_214013_b -> AbstractFurnaceTileEntity#43
    }

    /**
     * @return The liquid color of the given stack. -1 if not a (registered) liquid stack
     */
    private static int getLiquidColor(ItemStack s) {
        if (s != null) {
            return FluidUtil.getFluidHandler(s)
                    .map(handler ->
                            handler.drain(10000, false)
                    ).map(fluidStack -> fluidStack == null ? null : getFluidColor(fluidStack)).orElse(AlchemicalCauldronCraftingManager.getInstance().getLiquidColor(s));

        }
        return AlchemicalCauldronCraftingManager.getInstance().getLiquidColor(s);
    }

    /**
     * Retrieves the color of the given fluid stack.
     * Never returns -1 (0xFFFFFFFF)
     */
    private static int getFluidColor(FluidStack stack) {
        Fluid fluid = stack.getFluid();
//        if (fluid.equals(FluidRegistry.WATER)) { //TODO 1.14
//            return 0xC03040FF;
//        } else if (fluid.equals(FluidRegistry.LAVA)) {
//            return 0xFFFF5010;
//        }
        int color = fluid.getColor(stack);
        if (color == 0xFFFFFFFF) {
            color = 0xFFFFFFFE; //0xFFFFFFFF == -1 which makes our isLiquidCheck fail
        }
        return color;
    }

    /**
     * @return f the given stack is a (registed) liquid stack
     */
    private static boolean isLiquidStack(ItemStack stack) {
        return getLiquidColor(stack) != -1;
    }

    private int totalBurnTime = 0, burnTime = 0, cookTime = 0, totalCookTime = 0;
    private boolean cookingClient;
    private boolean burningClient;
    private int liquidColor;
    @OnlyIn(Dist.CLIENT)
    private ISoundReference boilingSound;
    /**
     * Can contain a recipe which can be cooked by the owner.
     * Is set when a {@link IAlchemicalCauldronRecipe#canBeCooked(int, ISkillHandler)} check succeeds.
     * As long as the current recipe is equals to this one, no new check is executed.
     * Thereby the owner can e.g. disconnect and the cauldron can continue cooking (as long as the recipe does not change).
     */
    @Nullable
    private IAlchemicalCauldronRecipe checkedRecipe;

    //TODO 1.13 Add AT for TileEntityFurnance#getBurnTime
    private static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            Item item = stack.getItem();
            int ret = stack.getBurnTime();
            return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? FurnaceTileEntity.getBurnTimes().getOrDefault(item, 0) : ret);
        }
    }
    /**
     * The UUID of the owner. Null on client, can be null on server.
     */
    private UUID ownerID;
    /**
     * The username of the owner.
     */
    private ITextComponent username;

//    public AlchemicalCauldronTileEntity() {
////        super(ModTiles.alchemical_cauldron, new InventorySlot[]{new InventorySlot(item -> false, 116, 35), new InventorySlot(AlchemicalCauldronTileEntity::isLiquidStack, 44, 17), new InventorySlot(68, 17), new InventorySlot(AbstractFurnaceTileEntity::isFuel, 56, 53)});
////    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    public boolean canUse(PlayerEntity player) {
        if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.basic_alchemy)) {
            if (isOwner(player)) {
                return true;
            } else {
                player.sendMessage(new TranslationTextComponent("tile.vampirism.alchemical_cauldron.other", getOwnerName()));
                return false;
            }
        }
        player.sendMessage(new TranslationTextComponent("tile.vampirism.alchemical_cauldron.cannot_use", getOwnerName()));
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLiquidColorClient() {
        return liquidColor;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", getOwnerName(), new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name"));
    }

    @Nonnull
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name");
    }

    /**
     * @return The name of the owner or "Unknown" if not yet set or synced
     */
    public ITextComponent getOwnerName() {
        if (username == null) {

            PlayerEntity player = getOwner();
            if (player != null) {
                username = player.getDisplayName();
            } else {
                return new StringTextComponent("Unknown");
            }
        }
        return username;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(Direction side) {
        switch (side) {
            case WEST:
                return SLOTS_WEST;
            case EAST:
                return SLOTS_EAST;
            case DOWN:
                return SLOTS_BOTTOM;
            case UP:
                return SLOTS_TOP;
            default:
                return new int[0];
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.write(new CompoundNBT());
        nbt.putBoolean("cooking", cookTime > 0 && isBurning());
        nbt.putBoolean("burning", burnTime > 0);
        nbt.putString("username", ITextComponent.Serializer.toJson(username));
        ItemStack liquidItem = getStackInSlot(SLOT_LIQUID);
        if (liquidItem != null) {
            nbt.put("liquidItem", liquidItem.write(new CompoundNBT()));
        }
        return nbt;
    }

    /**
     * @return burnTime>0 on server and cached boolean on client
     */
    public boolean isBurning() {
        if (world.isRemote) {
            return burningClient;
        }
        return burnTime > 0;
    }

    /**
     * @return cookTime>0 on server and cached boolean on client
     */
    public boolean isCooking() {
        if (world.isRemote) {
            return cookingClient;
        }
        return cookTime > 0;
    }

    /**
     * @return If there is a liquid inside
     */
    public boolean isFilled() {
        if (world.isRemote) {
            if (liquidColor == -1) return false;
        }
        return isStackInSlot(SLOT_LIQUID);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleUpdateTag(@Nonnull CompoundNBT nbt) {
        super.handleUpdateTag(nbt);
        cookingClient = nbt.getBoolean("cooking");
        burningClient = nbt.getBoolean("burning");
        if (nbt.contains("liquidItem")) {
            this.setInventorySlotContents(SLOT_LIQUID, ItemStack.read(nbt.getCompound("liquidItem")));
        } else {
            this.setInventorySlotContents(SLOT_LIQUID, ItemStack.EMPTY);
        }
        String u = nbt.getString("username");

        username = ITextComponent.Serializer.fromJson(u);

        liquidColor = getLiquidColor(getStackInSlot(SLOT_LIQUID));
        this.world.markForRerender(pos);
    }

    /**
     * Checks if the given player is the owner.
     * If none has been set yet, the given one becomes the owner.
     */
    public boolean isOwner(PlayerEntity player) {
        if (ownerID != null) {
            return ownerID.equals(player.getUniqueID());
        } else {
            setOwner(player);
            return true;
        }
    }

    public void markDirty(boolean sync) {
        super.markDirty();
        if (sync) {
            BlockState state = world.getBlockState(pos);
            this.world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);

    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        if (tagCompound.contains("burntime")) {
            this.burnTime = tagCompound.getInt("burntime");
            this.cookTime = tagCompound.getInt("cooktime");
            this.totalBurnTime = tagCompound.getInt("cooktime_total");
        }
        if (tagCompound.hasUniqueId("owner")) {
            ownerID = tagCompound.getUniqueId("owner");
        }
        if (tagCompound.contains("username")) {
            String u = tagCompound.getString("username");
            username = ITextComponent.Serializer.fromJson(u);
        }
        if (tagCompound.contains("bypass_recipecheck")) {
            ItemStack liquid = getStackInSlot(SLOT_LIQUID);
            ItemStack ingredient = getStackInSlot(SLOT_INGREDIENT);
            if (!liquid.isEmpty()) {
                checkedRecipe = AlchemicalCauldronCraftingManager.getInstance().findRecipe(liquid, ingredient);

            }
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        if (slot == SLOT_LIQUID && world instanceof ServerWorld) {
            ((ServerWorld) world).getChunkProvider().markBlockChanged(pos);
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("burntime", this.burnTime);
        compound.putInt("cooktime", this.cookTime);
        compound.putInt("cooktime_total", this.totalCookTime);
        if (ownerID != null) compound.putUniqueId("owner", ownerID);
        if (username != null) compound.putString("ownername", ITextComponent.Serializer.toJson(username));
        if (checkedRecipe != null) {
            compound.putBoolean("bypass_recipecheck", true);
        }
        return compound;
    }

    @Override
    public void tick() {
        boolean wasBurning = isBurning();
        boolean wasCooking = cookTime > 0;
        boolean dirty = false;
        if (wasBurning) {
            burnTime--;
        }

        if (!world.isRemote) {
            if (isBurning() || isStackInSlot(SLOT_LIQUID) && isStackInSlot(SLOT_FUEL)) {
                if (!isBurning() && canCook()) {
                    this.burnTime = getItemBurnTime(getStackInSlot(SLOT_FUEL));
                    if (isBurning()) {
                        decrStackSize(SLOT_FUEL, 1);
                        dirty = true;
                    }
                    totalBurnTime = burnTime;
                }
                if (isBurning() && this.canCook()) {
                    cookTime++;
                    if (cookTime >= totalCookTime) {
                        cookTime = 0;
                        this.totalCookTime = getCookTime();
                        this.finishCooking();
                        dirty = true;
                    }
                } else {
                    cookTime = 0;
                }

            } else if (!isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }
            if (wasBurning != this.isBurning()) {
                dirty = true;
            } else if (wasCooking != this.cookTime > 0) {
                dirty = true;
            }

        } else {
            //TODO particles
            //Do not check ISoundReference#isSoundPlaying for performance reason here. Also should not make any difference
            if (isCooking() && boilingSound == null && this.world.rand.nextInt(25) == 0) {
                boilingSound = VampLib.proxy.createSoundReference(ModSounds.boiling, SoundCategory.BLOCKS, getPos(), 0.015F, 7);

                boilingSound.startPlaying();
            } else if (!isCooking() && boilingSound != null) {
                boilingSound.stopPlaying();
                boilingSound = null;
            }

        }
        if (dirty) {
            this.markDirty(true);
        }
    }

    private boolean canCook() {
        if (!isStackInSlot(SLOT_LIQUID)) return false;
        IAlchemicalCauldronRecipe recipe = AlchemicalCauldronCraftingManager.getInstance().findRecipe(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
        if (recipe == null) return false;
        totalCookTime = recipe.getCookingTime();
        if (!canPlayerCook(recipe)) return false;
        if (!isStackInSlot(SLOT_RESULT)) return true;
        if (!getStackInSlot(SLOT_RESULT).isItemEqual(recipe.getOutput())) return false;
        int size = getStackInSlot(SLOT_RESULT).getCount() + recipe.getOutput().getCount();
        return size <= getInventoryStackLimit() && size <= getStackInSlot(SLOT_RESULT).getMaxStackSize();
    }

    /**
     * Checks if the owner can cook the recipe.
     * If a successful check was already performed for this recipe , the owner does not have to be online for this (unless the recipe changed in the meantime).
     */
    private boolean canPlayerCook(IAlchemicalCauldronRecipe recipe) {
        if (checkedRecipe != null && checkedRecipe.equals(recipe)) {
            return true;
        }
        PlayerEntity owner = getOwner();
        if (owner == null) return false;
        IHunterPlayer player = HunterPlayer.get(owner);
        ISkillHandler<IHunterPlayer> handler = player.getSkillHandler();
        boolean flag = recipe.canBeCooked(player.getLevel(), handler);
        if (flag) {
            checkedRecipe = recipe;
            return true;
        } else {
            checkedRecipe = null;
            return false;
        }
    }

    /**
     * Null on client side
     *
     * @return The owner of this cauldron if he is online.
     */
    private
    @Nullable
    PlayerEntity getOwner() {
        if (ownerID != null && !world.isRemote) {
            return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(ownerID);
        }
        return null;
    }

    private int getCookTime() {
        return 200;
    }

    public void setOwner(PlayerEntity player) {
        ownerID = player.getUniqueID();
        this.markDirty(true);
    }

    private void finishCooking() {
        if (canCook()) {
            IAlchemicalCauldronRecipe recipe = AlchemicalCauldronCraftingManager.getInstance().findRecipe(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
            if (!isStackInSlot(SLOT_RESULT)) {
                setInventorySlotContents(SLOT_RESULT, recipe.getOutput().copy());
            } else if (getStackInSlot(SLOT_RESULT).isItemEqual(recipe.getOutput())) {
                getStackInSlot(SLOT_RESULT).grow(recipe.getOutput().getCount());
            }
            if (recipe.isValidLiquidItem(getStackInSlot(SLOT_LIQUID))) {
                decrStackSize(SLOT_LIQUID, 1);
            } else {
                ItemStack fluidContainer = getStackInSlot(SLOT_LIQUID);
                FluidStack s = recipe.isValidFluidItem(fluidContainer);
                if (s != null) {
                    IFluidHandlerItem handler = (IFluidHandlerItem) FluidLib.getFluidItemCap(fluidContainer);
                    handler.drain(s, true);
                    setInventorySlotContents(SLOT_LIQUID, handler.getContainer());
                } else {
                    LOGGER.warn("Cooked item without valid input liquid (Recipe {}, Input {})", recipe, fluidContainer);
                }
            }
            decrStackSize(SLOT_INGREDIENT, 1);
        }
    }

    private boolean isStackInSlot(int slot) {
        return !getStackInSlot(slot).isEmpty();
    }
}
