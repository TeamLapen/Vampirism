package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.blocks.AlchemicalCauldronBlock;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * slots:  0: liquid, 1: ingredient, 2: result, 3: fuel
 */
public class AlchemicalCauldronTileEntity extends AbstractFurnaceTileEntity {
    private static final int[] SLOTS_DOWN = new int[]{0, 1, 2};
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_WEST = new int[]{1};
    private static final int[] SLOTS_FUEL = new int[]{3};

    @Nullable
    private UUID ownerID;
    @Nullable
    private String ownerName;
    private AlchemicalCauldronRecipe recipeChecked;

    @OnlyIn(Dist.CLIENT)
    private boolean boilingSound = false;

    public AlchemicalCauldronTileEntity() {
        super(ModTiles.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        if (super.canOpen(player)) {
            if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.basic_alchemy)) {
                if (ownerID == null) {
                    setOwnerID(player);
                    return true;
                } else if (ownerID.equals(player.getUniqueID())) {
                    return true;
                } else {
                    player.sendMessage(new TranslationTextComponent("text.vampirism.alchemical_cauldron.other", getOwnerName()));
                }
            } else {
                player.sendMessage(new TranslationTextComponent("text.vampirism.alchemical_cauldron.cannot_use", getOwnerName()));
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name");
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", ownerName, new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name"));
    }

    @OnlyIn(Dist.CLIENT)
    public int getLiquidColorClient() {
        return ModRecipes.getLiquidColor(this.items.get(3));
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
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = super.getUpdateTag();
        if (ownerID != null) compound.putUniqueId("owner", ownerID);
        if (ownerName != null) compound.putString("owner_name", ownerName);
        return compound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT compound) {
        super.handleUpdateTag(compound);
        ownerID = compound.hasUniqueId("owner") ? compound.getUniqueId("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
    }

    @Override
    public void markDirty() {
        if (world != null) {
            super.markDirty();
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);
    }

    @Override
    public void read(CompoundNBT compound) {
        ownerID = compound.hasUniqueId("owner") ? compound.getUniqueId("owner") : null;
        ownerName = compound.contains("owner_name") ? compound.getString("owner_name") : null;
        super.read(compound);
    }

    public void setOwnerID(PlayerEntity player) {
        ownerID = player.getUniqueID();
        ownerName = player.getDisplayName().getFormattedText();
        this.markDirty();
    }

    /**
     * copy of AbstractFurnaceTileEntity#tick() with modification
     */
    @Override
    public void tick() {
        if (world == null) return;
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (wasBurning) {
            this.furnaceData.set(0, this.furnaceData.get(0) - 1); // reduce burntime
        }

        if (!this.world.isRemote) {
            ItemStack itemstackFuel = this.items.get(3);
            if (this.isBurning() || !itemstackFuel.isEmpty() && !this.items.get(0).isEmpty() && !this.items.get(1).isEmpty()) {
                AlchemicalCauldronRecipe irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AlchemicalCauldronRecipe>) this.recipeType, this, this.world).orElse(null);
                if (!this.isBurning() && this.canSmelt(irecipe) && this.canPlayerCook(irecipe)) {
                    furnaceData.set(0, this.getBurnTime(itemstackFuel)); //Set burn time
                    furnaceData.set(1, furnaceData.get(0));
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

                if (this.isBurning() && this.canSmelt(irecipe) && this.canPlayerCook(irecipe)) {
                    furnaceData.set(2, furnaceData.get(2) + 1); //Increase cook time
                    if (furnaceData.get(2) == furnaceData.get(3)) { //If finished
                        furnaceData.set(2, 0);
                        furnaceData.set(3, this.func_214005_h());
                        this.finishCooking(irecipe);
                        dirty = true;
                    }
                } else {
                    furnaceData.set(2, 0); //Reset cook time
                }
            } else if (!this.isBurning() && furnaceData.get(2) > 0) {
                furnaceData.set(2, MathHelper.clamp(furnaceData.get(2) - 2, 0, furnaceData.get(3)));
            }

            if (wasBurning != this.isBurning()) {
                dirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()).with(AlchemicalCauldronBlock.LIQUID, this.items.get(0).isEmpty() ? 0 : this.isBurning() ? 2 : 1), 3);
            }
        } else {
            if (isCooking() && !boilingSound) {
                world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), ModSounds.boiling, SoundCategory.BLOCKS, 0.015F, 7, true);//TODO 1.14 stop sound
                boilingSound = true;
            }
        }

        if (dirty) {
            this.markDirty();
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (ownerID != null) {
            compound.putUniqueId("owner", ownerID);
        }
        if (ownerName != null) {
            compound.putString("owner_name", ownerName);

        }
        return super.write(compound);
    }

    @Nonnull
    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new AlchemicalCauldronContainer(id, player, this, this.furnaceData, world == null ? IWorldPosCallable.DUMMY : IWorldPosCallable.of(world, pos));
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron");
    }

    private boolean canPlayerCook(AlchemicalCauldronRecipe recipe) {
        if (world == null) return false;
        if (recipeChecked == recipe) return true;
        PlayerEntity playerEntity = this.world.getPlayerByUuid(ownerID);
        if (playerEntity == null) return false;
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
     *
     * @param recipe
     */
    private void finishCooking(AlchemicalCauldronRecipe recipe) {
        if (recipe != null && this.canSmelt(recipe) && canPlayerCook(recipe)) {
            ItemStack itemstackingredient = this.items.get(0);
            ItemStack itemstackfluid = this.items.get(1);
            ItemStack itemstack1result = recipe.getRecipeOutput();
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (this.world != null && !this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }
            itemstackingredient.shrink(1);
            itemstackfluid.shrink(1);
            recipeChecked = null;
        }
    }

    private boolean isBurning() {
        return this.furnaceData.get(0) > 0;
    }

    private boolean isCooking() {
        return this.furnaceData.get(2) > 0;
    }
}
