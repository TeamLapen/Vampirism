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
 * slots:  0: ingredient, 1: fuel, 2: result, 3: liquid
 */
public class AlchemicalCauldronTileEntity extends AbstractFurnaceTileEntity {//TODO 1.14 fluidsystem
    private static final int[] SLOTS_DOWN = new int[]{0, 2, 3};
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_WEST = new int[]{3};
    private static final int[] SLOTS_FUEL = new int[]{1};

    private UUID ownerID;
    private ITextComponent ownerName;
    private AlchemicalCauldronRecipe recipeChecked;

    @OnlyIn(Dist.CLIENT)
    private boolean boilingSound = false;

    public AlchemicalCauldronTileEntity() {
        super(ModTiles.alchemical_cauldron, ModRecipes.ALCHEMICAL_CAULDRON_TYPE);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new AlchemicalCauldronContainer(id, player, this, this.furnaceData);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN)
            return SLOTS_DOWN;
        else
            return side == Direction.UP ? SLOTS_UP : side == Direction.WEST ? SLOTS_WEST : SLOTS_FUEL;
    }

    @Override
    public boolean canOpen(PlayerEntity player) {
        if (super.canOpen(player)) {
            if (HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.basic_alchemy)) {
                if (ownerID == null) {
                    setOwnerID(player);
                }
                if (ownerID == player.getUniqueID()) {
                    return true;
                } else {
                    player.sendMessage(new TranslationTextComponent("tile.vampirism.alchemical_cauldron.other", ownerName));
                }
            } else {
                player.sendMessage(new TranslationTextComponent("tile.vampirism.alchemical_cauldron.cannot_use", ownerName));
            }
        }
        return false;
    }

    /**
     * copy of AbstractFurnaceTileEntity#tick() with modification
     */
    @Override
    public void tick() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        if (flag) {
            this.furnaceData.set(0, this.furnaceData.get(0) - 1);
        }

        if (!this.world.isRemote) {
            ItemStack itemstack = this.items.get(1);
            if (this.isBurning() || !itemstack.isEmpty() && !this.items.get(3).isEmpty() && !this.items.get(0).isEmpty()) {
                AlchemicalCauldronRecipe irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AlchemicalCauldronRecipe>) this.recipeType, this, this.world).orElse(null);
                if (!this.isBurning() && this.canSmelt(irecipe) && this.canPlayerCook(irecipe)) {
                    furnaceData.set(0, this.getBurnTime(itemstack));
                    furnaceData.set(1, furnaceData.get(0));
                    if (this.isBurning()) {
                        flag1 = true;
                        if (itemstack.hasContainerItem())
                            this.items.set(1, itemstack.getContainerItem());
                        else if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                this.items.set(1, itemstack.getContainerItem());
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt(irecipe) && this.canPlayerCook(irecipe)) {
                    furnaceData.set(2, furnaceData.get(2) + 1);
                    if (furnaceData.get(2) == furnaceData.get(3)) {
                        furnaceData.set(2, 0);
                        furnaceData.set(3, this.func_214005_h());
                        this.finishCooking(irecipe);
                        flag1 = true;
                    }
                } else {
                    furnaceData.set(2, 0);
                }
            } else if (!this.isBurning() && furnaceData.get(2) > 0) {
                furnaceData.set(2, MathHelper.clamp(furnaceData.get(2) - 2, 0, furnaceData.get(3)));
            }

            if (flag != this.isBurning()) {
                flag1 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())).with(AlchemicalCauldronBlock.LIQUID, this.items.get(3).isEmpty() ? 0 : this.isBurning() ? 2 : 1), 3);
            }
        } else {
            if (isCooking() && boilingSound && this.world.rand.nextInt(25) == 0) {
                world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), ModSounds.boiling, SoundCategory.BLOCKS, 0.015F, 7, true);//TODO 1.14 stop sound
                boilingSound = true;
            }
        }

        if (flag1) {
            this.markDirty();
        }

    }

    protected boolean canPlayerCook(AlchemicalCauldronRecipe recipe) {
        if (recipeChecked == recipe) return true;
        if (this.world.getPlayerByUuid(ownerID) == null) return false;
        HunterPlayer hunter = HunterPlayer.get(this.world.getPlayerByUuid(ownerID));
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
    protected void finishCooking(AlchemicalCauldronRecipe recipe) {
        if (recipe != null && this.canSmelt(recipe) && canPlayerCook(recipe)) {
            ItemStack itemstackingredient = this.items.get(0);
            ItemStack itemstackfluid = this.items.get(3);
            ItemStack itemstack1result = recipe.getRecipeOutput();
            ItemStack itemstackoutput = this.items.get(2);
            if (itemstackoutput.isEmpty()) {
                this.items.set(2, itemstack1result.copy());
            } else if (itemstackoutput.getItem() == itemstack1result.getItem()) {
                itemstackoutput.grow(itemstack1result.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }
            itemstackingredient.shrink(1);
            itemstackfluid.shrink(1);
            recipeChecked = null;
        }
    }

    public void setOwnerID(PlayerEntity player) {
        ownerID = player.getUniqueID();
        ownerName = player.getDisplayName();
        this.markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("owner", ownerID);
        compound.putString("owner_name", ownerName.toString());
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        ownerID = compound.getUniqueId("owner");
        ownerName = new StringTextComponent(compound.getString("owner_name"));
        super.read(compound);
    }


    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = super.getUpdateTag();
        compound.putUniqueId("owner", ownerID);
        compound.putString("owner_name", ownerName.toString());
        return compound;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundNBT compound) {
        super.handleUpdateTag(compound);
        ownerID = compound.getUniqueId("owner");
        ownerName = new StringTextComponent(compound.getString("owner_name"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);
    }


    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.alchemical_cauldron");
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", ownerName, new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name"));
    }

    @Nonnull
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("tile.vampirism.alchemical_cauldron.name");
    }

    private boolean isBurning() {
        return this.furnaceData.get(1) > 0;
    }

    private boolean isCooking() {
        return this.furnaceData.get(2) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLiquidColorClient() {
        return ModRecipes.getLiquidColor(this.items.get(3));
    }

    public ITextComponent getOwnerName() {
        return ownerName != null ? ownerName : new StringTextComponent("Unknown");
    }
}
