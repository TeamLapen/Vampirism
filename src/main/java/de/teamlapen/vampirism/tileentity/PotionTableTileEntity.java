package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.PotionTableContainer;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;


public class PotionTableTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity, INamedContainerProvider {

    /*
     * 0: Fuel
     * 1: Extra ingredient
     * 2: Main (vanilla) ingredient
     * 3-5: Main bottle slots
     * 6-7: Extra bottle slots
     */
    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2};
    private static final int[] SLOTS_FOR_DOWN = new int[]{3, 4, 5, 1, 2};
    private static final int[] SLOTS_FOR_DOWN_EXTENDED = new int[]{3, 4, 5, 6, 7, 1, 2};
    private static final int[] OUTPUT_SLOTS = new int[]{3, 4, 5, 0};
    private static final int[] OUTPUT_SLOTS_EXTENDED = new int[]{3, 4, 5, 6, 7, 0};
    private final BrewingCapabilities config = new BrewingCapabilities();
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    @Nullable
    private UUID ownerID;
    @Nullable
    private ITextComponent ownerName;
    private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(8, ItemStack.EMPTY);
    private int brewTime;
    private Item ingredientID;
    private Item extraIngredientID;
    private int fuel;
    protected final IIntArray syncedProperties = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return PotionTableTileEntity.this.brewTime;
                case 1:
                    return PotionTableTileEntity.this.fuel;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    PotionTableTileEntity.this.brewTime = value;
                    break;
                case 1:
                    PotionTableTileEntity.this.fuel = value;
                    break;
            }

        }

        public int getCount() {
            return 2;
        }
    };

    public PotionTableTileEntity() {
        super(ModTiles.POTION_TABLE.get());
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        if (super.canOpen(player)) {
            return HunterPlayer.getOpt(player).map(hp -> {
                if (hp.getLevel() > 0) {
                    if (ownerID == null) {
                        setOwnerID(player);
                        this.config.deriveFromHunter(hp);
                        return true;
                    } else if (ownerID.equals(player.getUUID())) {
                        this.config.deriveFromHunter(hp);
                        return true;
                    } else {
                        player.displayClientMessage(new TranslationTextComponent("text.vampirism.potion_table.other", getOwnerName()), true);
                    }
                } else {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.potion_table.cannot_use", getOwnerName()), true);
                }
                return false;

            }).orElse(false);

        }
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 2) {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
        } else {
            Item item = stack.getItem();
            if (index == 0) {
                return item == Items.BLAZE_POWDER;
            } else {
                return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack) && this.getItem(index).isEmpty();
            }
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (index == 1 || index == 2) {
            return stack.getItem() == Items.GLASS_BOTTLE;
        } else {
            return true;
        }
    }

    @Override
    public void clearContent() {
        this.brewingItemStacks.clear();
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.vampirism.potion_table.display", ownerName, new TranslationTextComponent("tile.vampirism.potion_table"));
    }

    @Nonnull
    public ITextComponent getOwnerName() {
        return ownerName == null ? new StringTextComponent("Unknown") : ownerName;
    }

    @Override
    public int getContainerSize() {
        return this.brewingItemStacks.size();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) {
            return SLOTS_FOR_UP;
        } else {
            return side == Direction.DOWN ? config.multiTaskBrewing ? SLOTS_FOR_DOWN_EXTENDED : SLOTS_FOR_DOWN : config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS;
        }
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.brewingItemStacks.size() ? this.brewingItemStacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.brewingItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean isExtended() {
        return this.config.isMultiTaskBrewing();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.brewingItemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.brewingItemStacks);
        this.brewTime = compound.getShort("BrewTime");
        this.fuel = compound.getByte("Fuel");
        this.config.fromByte(compound.getByte("config"));
        this.ownerID = compound.hasUUID("owner") ? compound.getUUID("owner") : null;
        this.ownerName = compound.contains("owner_name") ? ITextComponent.Serializer.fromJsonLenient(compound.getString("owner_name")) : null;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(this.brewingItemStacks, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(this.brewingItemStacks, index);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putShort("BrewTime", (short) this.brewTime);
        ItemStackHelper.saveAllItems(compound, this.brewingItemStacks);
        compound.putByte("Fuel", (byte) this.fuel);
        compound.putByte("config", this.config.toByte());
        if (ownerID != null) {
            compound.putUUID("owner", ownerID);
            compound.putString("owner_name", ITextComponent.Serializer.toJson(ownerName));
        }
        return compound;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.size()) {
            this.brewingItemStacks.set(index, stack);
        }

    }

    public void setOwnerID(PlayerEntity player) {
        ownerID = player.getUUID();
        ownerName = player.getDisplayName();
        this.setChanged();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (int x = 0; x < handlers.length; x++)
            handlers[x].invalidate();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (!hasLevel()) return false;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void tick() {
        ItemStack itemstack = this.brewingItemStacks.get(0);
        if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = 20;
            itemstack.shrink(1);
            this.setChanged();
        }

        //Periodically update table capabilities if player is loaded
        if (ownerID != null && this.hasLevel() && this.level.getGameTime() % 64 == 0) {
            PlayerEntity owner = this.level.getPlayerByUUID(ownerID);
            if (owner != null) HunterPlayer.getOpt(owner).ifPresent(this.config::deriveFromHunter);
        }

        boolean canBrew = this.canBrew();
        boolean isBrewing = this.brewTime > 0;
        if (isBrewing) {
            --this.brewTime;
            if (this.brewTime == 0 && canBrew) { //Finish brewing
                this.brewPotions();
                this.setChanged();
            } else if (!canBrew || this.ingredientID != this.brewingItemStacks.get(2).getItem() || this.extraIngredientID != this.brewingItemStacks.get(1).getItem()) {//Abort brewing if ingredients changed
                this.brewTime = 0;
                this.setChanged();
            }
        } else if (canBrew && this.fuel > 0) {
            --this.fuel;
            this.brewTime = config.isSwiftBrewing() ? 400 : 200;
            this.ingredientID = this.brewingItemStacks.get(2).getItem();
            this.extraIngredientID = this.brewingItemStacks.get(1).getItem();
            this.setChanged();
        }

    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new PotionTableContainer(id, player, IWorldPosCallable.create(this.level, this.getBlockPos()), this, this.config.multiTaskBrewing, syncedProperties);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.brewing");
    }

    private void brewPotions() {
        ItemStack ingredientStack = this.brewingItemStacks.get(2);
        ItemStack extraIngredient = this.brewingItemStacks.get(1);

        boolean brewed = VampirismAPI.extendedBrewingRecipeRegistry().brewPotions(brewingItemStacks, ingredientStack, extraIngredient, this.config, this.config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS, true);

        if (!brewed) {
            NonNullList<ItemStack> copiedBrewingItemStack = NonNullList.of(ItemStack.EMPTY, this.brewingItemStacks.get(3).copy(), this.brewingItemStacks.get(4).copy(), this.brewingItemStacks.get(5).copy(), this.brewingItemStacks.get(2).copy(), this.brewingItemStacks.get(0).copy());
            if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(copiedBrewingItemStack)) {
                this.brewingItemStacks.set(3, copiedBrewingItemStack.get(0));
                this.brewingItemStacks.set(4, copiedBrewingItemStack.get(1));
                this.brewingItemStacks.set(5, copiedBrewingItemStack.get(2));
                this.brewingItemStacks.set(2, copiedBrewingItemStack.get(3));
                this.brewingItemStacks.set(0, copiedBrewingItemStack.get(4));
                return;
            }
            VampirismAPI.extendedBrewingRecipeRegistry().brewPotions(brewingItemStacks, ingredientStack, extraIngredient, this.config, this.config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS, false);
            copiedBrewingItemStack = NonNullList.of(ItemStack.EMPTY, this.brewingItemStacks.get(3).copy(), this.brewingItemStacks.get(4).copy(), this.brewingItemStacks.get(5).copy(), this.brewingItemStacks.get(2).copy(), this.brewingItemStacks.get(0).copy());
            net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(brewingItemStacks);
            this.brewingItemStacks.set(3, copiedBrewingItemStack.get(0));
            this.brewingItemStacks.set(4, copiedBrewingItemStack.get(1));
            this.brewingItemStacks.set(5, copiedBrewingItemStack.get(2));
            this.brewingItemStacks.set(2, copiedBrewingItemStack.get(3));
            this.brewingItemStacks.set(0, copiedBrewingItemStack.get(4));
        }


        BlockPos blockpos = this.getBlockPos();
        if (ingredientStack.hasContainerItem()) {
            ItemStack itemstack1 = ingredientStack.getContainerItem();
            if (ingredientStack.isEmpty()) {
                ingredientStack = itemstack1;
            } else if (!this.level.isClientSide) {
                InventoryHelper.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
            }
        }
        if (extraIngredient.hasContainerItem()) {
            ItemStack itemstack1 = extraIngredient.getContainerItem();
            if (extraIngredient.isEmpty()) {
                extraIngredient = itemstack1;
            } else if (!this.level.isClientSide) {
                InventoryHelper.dropItemStack(this.level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), itemstack1);
            }
        }

        this.brewingItemStacks.set(2, ingredientStack);
        this.brewingItemStacks.set(1, extraIngredient);
        this.level.levelEvent(1035, blockpos, 0);
    }

    private boolean canBrew() {
        ItemStack extraStack = this.brewingItemStacks.get(1);
        ItemStack ingredientStack = this.brewingItemStacks.get(2);
        if (!ingredientStack.isEmpty())
            return VampirismAPI.extendedBrewingRecipeRegistry().canBrew(brewingItemStacks, ingredientStack, extraStack, this.config, config.multiTaskBrewing ? OUTPUT_SLOTS_EXTENDED : OUTPUT_SLOTS); // divert to VanillaBrewingRegistry

        return false;
    }

    protected static class BrewingCapabilities implements IExtendedBrewingRecipeRegistry.IExtendedBrewingCapabilities {
        boolean durableBrewing;
        boolean concentratedBrewing;
        boolean swiftBrewing;
        boolean masterBrewing;
        boolean efficientBrewing;
        boolean multiTaskBrewing;

        public void deriveFromHunter(IHunterPlayer player) {
            ISkillHandler<IHunterPlayer> manager = player.getSkillHandler();
            durableBrewing = manager.isSkillEnabled(HunterSkills.DURABLE_BREWING.get()) || manager.isSkillEnabled(HunterSkills.CONCENTRATED_DURABLE_BREWING.get());
            concentratedBrewing = manager.isSkillEnabled(HunterSkills.CONCENTRATED_BREWING.get()) || manager.isSkillEnabled(HunterSkills.CONCENTRATED_DURABLE_BREWING.get());
            swiftBrewing = manager.isSkillEnabled(HunterSkills.SWIFT_BREWING.get());
            masterBrewing = manager.isSkillEnabled(HunterSkills.MASTER_BREWER.get());
            efficientBrewing = manager.isSkillEnabled(HunterSkills.EFFICIENT_BREWING.get());
            multiTaskBrewing = manager.isSkillEnabled(HunterSkills.MULTITASK_BREWING.get());
        }

        public void fromByte(byte d) {
            this.durableBrewing = (d & (0b1)) > 0;
            this.concentratedBrewing = (d & (0b1 << 1)) > 0;
            this.swiftBrewing = (d & (0b1 << 2)) > 0;
            this.masterBrewing = (d & (0b1 << 3)) > 0;
            this.efficientBrewing = (d & (0b1 << 4)) > 0;
            this.multiTaskBrewing = (d & (0b1 << 5)) > 0;
        }

        @Override
        public boolean isConcentratedBrewing() {
            return concentratedBrewing;
        }

        @Override
        public boolean isDurableBrewing() {
            return durableBrewing;
        }

        @Override
        public boolean isEfficientBrewing() {
            return efficientBrewing;
        }

        @Override
        public boolean isMasterBrewing() {
            return masterBrewing;
        }

        @Override
        public boolean isMultiTaskBrewing() {
            return multiTaskBrewing;
        }

        @Override
        public boolean isSwiftBrewing() {
            return swiftBrewing;
        }

        public void reset() {
            durableBrewing = concentratedBrewing = swiftBrewing = masterBrewing = efficientBrewing = multiTaskBrewing = false;
        }

        public byte toByte() {
            byte d = 0;
            if (durableBrewing) d |= 0b1;
            if (concentratedBrewing) d |= (0b1 << 1);
            if (swiftBrewing) d |= (0b1 << 2);
            if (masterBrewing) d |= (0b1 << 3);
            if (efficientBrewing) d |= (0b1 << 4);
            if (multiTaskBrewing) d |= (0b1 << 5);
            return d;
        }

    }
}
